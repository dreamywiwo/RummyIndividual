package ensambladores;

// --- INFRAESTRUCTURA Y RED ---
import ClaseDispatcher.ColaDispatcher;
import ClaseDispatcher.Dispatcher;
import ClaseDispatcher.SocketOut;
import claseReceptor.ColaReceptor;
import claseReceptor.Receptor;
import claseReceptor.SocketIN;
import com.mycompany.conexioninterfaces.IDispatcher;
import itson.serializer.implementacion.JsonSerializer;

// --- PRODUCER (SALIDA) ---
import itson.producerjugador.emitters.ConfigurarPartidaEmitter;
import itson.producerjugador.emitters.InicializarJuegoEmitter;
import itson.producerjugador.emitters.JugarTurnoEmitter;
import itson.producerjugador.facade.IProducerJugador;
import itson.producerjugador.facade.ProducerJugador;

// --- TRADUCER (ENTRADA) ---
import itson.traducerjugador.facade.TraducerJugador;
import itson.traducerjugador.mappers.EventMapper;

// --- DTOs Y UTILS ---
import itson.rummydtos.JugadorDTO;
import itson.rummypresentacion.utils.ListenerProxy;
import itson.rummypresentacion.utils.SesionCliente;
import itson.rummypresentacion.utils.TipoVista;

// --- MVC 1: CONFIGURAR PARTIDA ---
import itson.configurarpartida.controlador.ControladorConfigurarPartida;
import itson.configurarpartida.modelo.ModeloConfiguracion;
import itson.configurarpartida.vista.UI_ConfigurarPartida;
import itson.configurarpartida.vista.UI_MenuRummy;

// --- MVC 2: EJERCER TURNO ---
import itson.ejercerturno.controlador.ControladorTurno;
import itson.ejercerturno.modelo.ModeloEjercerTurno;
import itson.ejercerturno.vista.UI_TurnoJugador;

public class EnsambladorCliente {
    
    /**
     * Construye toda la aplicación cliente, conecta los MVCs y arranca la red.
     */
    public void iniciarAplicacion(String ipBroker, int puertoBroker, String miIp, int miPuerto, String miId) {
        
        // 1. CAPA DE INFRAESTRUCTURA DE RED (SOCKETS & SERIALIZADORES)
        JsonSerializer jsonSerializer = new JsonSerializer();
        
        // --- SALIDA (Socket Out) ---
        SocketOut socketOut = new SocketOut();
        socketOut.start();
        ColaDispatcher colaDispatcher = new ColaDispatcher();
        colaDispatcher.attach(socketOut);
        IDispatcher dispatcher = new Dispatcher(colaDispatcher);
        
        // Emitters
        JugarTurnoEmitter emitterJugar = new JugarTurnoEmitter(jsonSerializer, dispatcher, ipBroker, puertoBroker);
        InicializarJuegoEmitter emitterInit = new InicializarJuegoEmitter(jsonSerializer, dispatcher, ipBroker, puertoBroker);
        ConfigurarPartidaEmitter emitterConfigurar = new ConfigurarPartidaEmitter(jsonSerializer, dispatcher, ipBroker, puertoBroker);
        
        // Facade Producer
        IProducerJugador producer = new ProducerJugador(emitterJugar, emitterInit, emitterConfigurar, miId);
        
        // 2. PREPARACIÓN DE ENTRADA (LISTENER PROXY)
        
        // El Proxy es el intermediario que decide a qué modelo enviar los mensajes
        ListenerProxy listenerProxy = new ListenerProxy();
        
        // El EventMapper siempre le habla al Proxy
        EventMapper eventMapper = new EventMapper(jsonSerializer);
        eventMapper.setListener(listenerProxy); 
        eventMapper.setJugadorId(miId);
        
        // 3. SESIÓN COMPARTIDA Y DATOS INICIALES
        
        // Mock de Perfil (Registro Simulado)
        String rutaAvatar = (miPuerto % 2 == 0) ? "/imageFish.png" : "/imageBun.png";
        String nombreJugador = "Jugador " + (miPuerto % 100); 
        JugadorDTO perfil = new JugadorDTO(miId, nombreJugador, rutaAvatar);
        
        // Creamos la sesión para pasar dependencias entre controladores
        SesionCliente sesion = new SesionCliente(producer, perfil, listenerProxy);

        // 4. CONSTRUCCIÓN MVC 2: EJERCER TURNO (DESTINO)
        // Creamos este MVC pero lo mantenemos oculto hasta que se configure la partida.
        
        ModeloEjercerTurno modeloJuego = new ModeloEjercerTurno(producer);
        modeloJuego.setJugadorLocal(miId);
        
        ControladorTurno ctrlJuego = new ControladorTurno(modeloJuego, sesion);
        
        UI_TurnoJugador vistaJuego = new UI_TurnoJugador(ctrlJuego, perfil);
        
        // Suscripción Observer
        modeloJuego.suscribir(vistaJuego);
        
        // Estado inicial: Oculto
        vistaJuego.setVisible(false);

        // 5. CONSTRUCCIÓN MVC 1: CONFIGURAR PARTIDA (ORIGEN)
        
        ModeloConfiguracion modeloConfig = new ModeloConfiguracion(producer);
        
        ControladorConfigurarPartida ctrlConfig = new ControladorConfigurarPartida(modeloConfig);
        
        ctrlConfig.setSiguienteControlador(ctrlJuego);
        
        UI_MenuRummy vistaMenu = new UI_MenuRummy(ctrlConfig);
        UI_ConfigurarPartida vistaConfig = new UI_ConfigurarPartida(ctrlConfig);
        
        // Suscripción Observer
        modeloConfig.suscribir(vistaMenu);
        modeloConfig.suscribir(vistaConfig);
        
        // Configuración inicial del Proxy: Escuchar eventos de configuración (errores)
        listenerProxy.activarModoConfiguracion(modeloConfig);

        // 6. ARRANCAR RED ENTRANTE (SOCKET IN)
        
        TraducerJugador traducer = new TraducerJugador(jsonSerializer, eventMapper);
        Receptor receptor = new Receptor(traducer);
        ColaReceptor colaReceptor = new ColaReceptor();
        colaReceptor.attach(receptor);
        
        SocketIN socketIn = new SocketIN(miPuerto, colaReceptor);
        socketIn.start();
        
        System.out.println("Ensamblador: Cliente " + nombreJugador + " (" + miId + ") iniciado en " + miIp + ":" + miPuerto);

        // 7. INICIO DEL FLUJO
        
        // Enviamos registro al broker 
        emitterInit.emitirRegistroJugadorEvent(miId, miIp, miPuerto); 
        System.out.println("Ensamblador: Solicitud de registro enviada.");
        
        // Mostramos el menú inicial
        modeloConfig.cambiarVista(TipoVista.MENU_PRINCIPAL);
    }
}