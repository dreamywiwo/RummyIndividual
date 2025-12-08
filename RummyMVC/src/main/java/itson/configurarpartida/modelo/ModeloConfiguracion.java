/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.configurarpartida.modelo;

import itson.producerjugador.facade.IProducerJugador;
import itson.rummylistener.interfaces.IConfiguracionListener;
import itson.rummypresentacion.utils.TipoVista;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dana Chavez
 */
public class ModeloConfiguracion implements IModeloConfiguracion, ISubjectConfiguracion, IConfiguracionListener {

    private IProducerJugador producer;
    // Lista de observadores
    private List<IObserverConfiguracion> observers;

    private TipoVista vistaActual = TipoVista.MENU_PRINCIPAL;
    private String mensajeError;

    public ModeloConfiguracion(IProducerJugador producer) {
        this.producer = producer;
        this.observers = new ArrayList<>();
    }

    // LLAMADAS DESDE EL CONTROL
    public void cambiarVista(TipoVista nuevaVista) {
        this.vistaActual = nuevaVista;
        this.mensajeError = null; 
        notificarObservers();
    }

    public void volverAlMenu() {
        this.vistaActual = TipoVista.MENU_PRINCIPAL;
        notificarObservers();
    }

    public void enviarConfiguracionPartida(int maxNumFichas, int cantidadComodines) {
        try {
            producer.configurarPartida(maxNumFichas, cantidadComodines);

            // this.vistaActual = TipoVista.REGISTRAR_JUGADOR; // una vez que este implementado
            // notificarObservers();
        } catch (Exception e) {
            this.mensajeError = "Error al comunicar con el servidor";
            notificarObservers();
        }
    }

    // GETTER PARA IMODELO
    @Override
    public TipoVista getVistaActual() {
        return vistaActual;
    }

    @Override
    public String getMensajeError() {
        return mensajeError;
    }

    // OBSERVER
    @Override
    public void suscribir(IObserverConfiguracion observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void notificar(IObserverConfiguracion observer) {
        observer.update(this);
    }

    @Override
    public void notificarObservers() {
        for (IObserverConfiguracion observer : observers) {
            observer.update(this);
        }
    }

    // METODOS LISTENER
    @Override
    public void recibirError(String mensaje) {
        this.mensajeError = mensaje;
        notificarObservers();
    }
}
