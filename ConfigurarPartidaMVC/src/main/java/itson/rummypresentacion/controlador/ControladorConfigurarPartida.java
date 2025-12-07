/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.controlador;

import itson.producerjugador.facade.IProducerJugador;
import itson.rummypresentacion.modelo.Modelo;
import itson.rummypresentacion.utils.TipoVista;

/**
 *
 * @author Dana Chavez
 */
public class ControladorConfigurarPartida {
    
    private Modelo modelo;

    public ControladorConfigurarPartida(Modelo modelo, IProducerJugador producerJugador) {
        this.modelo = modelo;
    }
    
    // Navegación
    public void irAConfigurarPartida(){
        modelo.cambiarVista(TipoVista.CONFIGURAR_PARTIDA);
    }
    
    public void solicitarVolverMenu(){
        modelo.volverAlMenu();
    }
    
    // Acción de Negocio 
    public void confirmarConfiguracion(int maxNumFichas, int cantidadComodines){
        modelo.enviarConfiguracionPartida(maxNumFichas, cantidadComodines); 
    }
}
