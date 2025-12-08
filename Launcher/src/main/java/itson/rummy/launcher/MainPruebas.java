/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package itson.rummy.launcher;

import ensambladores.EnsambladorCliente;
import ensambladores.EnsambladorDominio;
import ensambladores.EnsambladorServidor;
import itson.dominiorummy.entidades.Jugador;
// Ya no importamos UI_TurnoJugador porque el Main no la toca directamente
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

public class MainPruebas {

    private static final String IP_LOCALHOST = "127.0.0.1";

    private static final int PUERTO_BROKER = 9999;
    private static final int PUERTO_DOMINIO = 9998;

    private static final int PUERTO_JUGADOR_1 = 9001;
    private static final int PUERTO_JUGADOR_2 = 9002;

    public static void main(String[] args) {

        // 1. Iniciar Broker
        new Thread(() -> {
            try {
                System.out.println("[BROKER] Iniciando...");
                EnsambladorServidor servidor = new EnsambladorServidor();
                servidor.iniciarBroker(PUERTO_BROKER);
            } catch (Exception e) {
                System.err.println("Error en Broker: " + e.getMessage());
            }
        }).start();

        esperar(1000);

        // 2. Crear Jugadores 
        List<Jugador> listaJugadores = new ArrayList<>();
        Jugador jugador1 = new Jugador("Jugador1"); 
        Jugador jugador2 = new Jugador("Jugador2");
        
        listaJugadores.add(jugador1);
        listaJugadores.add(jugador2);

        System.out.println("[DOMINIO] IDs Generados -> J1: " + jugador1.getId() + " | J2: " + jugador2.getId());

        // 3. Iniciar Dominio (Server)
        System.out.println("[DOMINIO] Configurando lógica del servidor...");
        EnsambladorDominio ensambladorDominio = new EnsambladorDominio();
        try {
            // Esto prepara el juego mockeado en el servidor
            ensambladorDominio.iniciarJuego(IP_LOCALHOST, PUERTO_BROKER, PUERTO_DOMINIO, listaJugadores);
        } catch (Exception e) {
            System.err.println("Error al configurar el Dominio: " + e.getMessage());
        }

        // 4. Lanzar Clientes
        lanzarCliente(jugador1.getId(), PUERTO_JUGADOR_1);
        lanzarCliente(jugador2.getId(), PUERTO_JUGADOR_2);
        
        esperar(3000);

        // 5. Arrancar el juego en el servidor
        // Nota: Como los clientes inician en el Menú, el Proxy (Listener) estará en "Modo Configuración".
        // Los eventos que mande el dominio ahora (como "Turno Iniciado") serán ignorados por el cliente
        // hasta que el usuario navegue a la pantalla de juego.
        System.out.println("[MAIN] Clientes en Menú. Iniciando lógica de partida en background...");
        ensambladorDominio.comenzarPartida();
    }

    private static void lanzarCliente(String id, int puertoEscucha) {
        SwingUtilities.invokeLater(() -> {
            EnsambladorCliente ensamblador = new EnsambladorCliente();

            ensamblador.iniciarAplicacion(
                    IP_LOCALHOST,   // IP Broker
                    PUERTO_BROKER,  // Puerto Broker
                    IP_LOCALHOST,   // Mi IP
                    puertoEscucha,  // Mi Puerto
                    id              // Mi ID
            );
        });
    }

    private static void esperar(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}