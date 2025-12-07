/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.producerjugador.emitters;

import com.mycompany.conexioninterfaces.IDispatcher;
import itson.rummyeventos.acciones.PartidaConfiguradaEvent;
import itson.serializer.implementacion.JsonSerializer;

/**
 *
 * @author Dana Chavez
 */
public class ConfigurarPartidaEmitter {
    private final JsonSerializer jsonSerializer;
    private final IDispatcher dispatcher;
    private final String brokerIp;
    private final int brokerPort;

    public ConfigurarPartidaEmitter(JsonSerializer jsonSerializer, IDispatcher dispatcher, String brokerIp, int brokerPort) {
        this.jsonSerializer = jsonSerializer;
        this.dispatcher = dispatcher;
        this.brokerIp = brokerIp;
        this.brokerPort = brokerPort;
    }

    public void emitirPartidaConfiguradaEvent(int maxNumFichas, int cantidadComodines) {
        PartidaConfiguradaEvent event = new PartidaConfiguradaEvent(maxNumFichas, cantidadComodines);
        String json = jsonSerializer.serialize(event);
        dispatcher.enviar(json, brokerPort, brokerIp);    
    }
       
}
