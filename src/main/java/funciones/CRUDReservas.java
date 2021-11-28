package funciones;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import models.Reserva;
import models.ReservaListResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import services.DynamoDBServices;

import java.io.*;

public class CRUDReservas implements RequestStreamHandler {

    private DynamoDBServices dynamoDBServices = new DynamoDBServices();
    private Gson gson = new Gson();

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {

        //Objetos para el control de la salida.
        JSONParser parser = new JSONParser();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String cuerpoRecibido = null;
        JSONObject responseJson = new JSONObject();
        String salida = "";
        Reserva reserva = null;
        //
        try {
            //Parseando el objeto.
            JSONObject jsonObject = (JSONObject) parser.parse(reader);

            //Ver la salida por la consola sobre la trama enviada por el APIGateway
            context.getLogger().log("" + jsonObject.toJSONString());

            //Recuperando el metodo de acceso de la llamada del API.
            if (jsonObject.get("requestContext") == null) {
                throw new IllegalArgumentException("No respesta el API de entrada");
            }

            String metodoHttp = ((JSONObject) (jsonObject.get("requestContext"))).get("httpMethod").toString();
            context.getLogger().log(String.format("METODO HTTP1 = %s, ", metodoHttp));
//            context.getLogger().log(String.format("METODO HTTP2 = %s, ", ((JSONObject) jsonObject.get("requestContext")).get("httpMethod").toString()));

//            String metodoHttp = "POST";

            //Realizando la operacion
            switch (metodoHttp) {
                case "GET":
                    ReservaListResponse listarEstudiantesResponse = dynamoDBServices.listar(context);
                    salida = gson.toJson(listarEstudiantesResponse);
                    break;
                case "POST":
                    reserva = jsonToReserva(jsonObject);
                    dynamoDBServices.insert(reserva, context);
                    salida = gson.toJson(reserva);
                    break;
                case "PUT":
                    reserva = jsonToReserva(jsonObject);
                    dynamoDBServices.delete(reserva, context);
                    dynamoDBServices.insert(reserva, context);
                    salida = gson.toJson(reserva);
                    break;
                case "DELETE":
                    reserva = jsonToReserva(jsonObject);
                    dynamoDBServices.delete(reserva, context);
                    salida = gson.toJson(reserva);
                    break;
                default:
                    break;
            }

            //La información enviada por el metodo Post o Put estará disponible en la propiedad body:
            if (jsonObject.get("body") != null) {
                cuerpoRecibido = jsonObject.get("body").toString();
            }

            //Respuesta en el formato esperado:
            JSONObject responseBody = new JSONObject();
            responseBody.put("data", JsonParser.parseString(salida));
            responseBody.put("entrada", cuerpoRecibido);

            JSONObject headerJson = new JSONObject();
            headerJson.put("mi-header", "Mi propio header");
            headerJson.put("Content-Type", "application/json");
            headerJson.put("Access-Control-Allow-Origin", "*");

            responseJson.put("statusCode", 200);
            responseJson.put("headers", headerJson);
            responseJson.put("body", responseBody.toString());

        } catch (ParseException ex) {
            responseJson.put("statusCode", 400);
            responseJson.put("exception", ex);
        }

        //Salida
        OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
        writer.write(responseJson.toString());
        writer.close();
    }

    private Reserva jsonToReserva(JSONObject json) throws IllegalArgumentException {
        if (json.get("body") == null) {
            throw new IllegalArgumentException("No envio el cuerpo en la trama.");
        }
        Reserva reserva = gson.fromJson(json.get("body").toString(), Reserva.class);
        return reserva;
    }

}
