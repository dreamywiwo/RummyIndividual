/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.configurarpartida.controlador;

import itson.configurarpartida.modelo.ModeloConfiguracion;
import itson.ejercerturno.controlador.ControladorTurno;
import itson.rummypresentacion.utils.TipoVista;

/**
 *
 * @author Dana Chavez
 */
public class ControladorConfigurarPartida {

    private ModeloConfiguracion modelo;
    private ControladorTurno siguienteControlador;

    public ControladorConfigurarPartida(ModeloConfiguracion modelo) {
        this.modelo = modelo;
    }
    
    public void setSiguienteControlador(ControladorTurno siguienteControlador) {
        this.siguienteControlador = siguienteControlador;
    }

    // Navegación
    public void irAConfigurarPartida() {
        modelo.cambiarVista(TipoVista.CONFIGURAR_PARTIDA);
    }

    public void volverAlMenu() {
        modelo.volverAlMenu();
    }

    // Acción de Negocio 
    public void confirmarConfiguracion(int maxNumFichas, int cantidadComodines){
        modelo.enviarConfiguracionPartida(maxNumFichas, cantidadComodines);
        
        if (siguienteControlador != null) {
            System.out.println("[CtrlConfig] Cambiando a vista de juego...");
            
            // Ocultar configuración
            modelo.cambiarVista(null); 
            
            // Iniciar juego
            siguienteControlador.iniciarJuego();
        }
    }
}
