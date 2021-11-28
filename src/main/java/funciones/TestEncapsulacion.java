package funciones;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.google.gson.Gson;
import models.Reserva;
import models.ReservaListResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import services.DynamoDBServices;

import java.io.IOException;

//didnt work
public class TestEncapsulacion {

    private DynamoDBServices dynamoDBServices = new DynamoDBServices();
    private Gson gson = new Gson();

    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent request, Context context) throws IOException {

        //Objetos para el control de la salida.
        JSONParser parser = new JSONParser();
        Reserva reserva = null;
        APIGatewayV2HTTPResponse reponse = new APIGatewayV2HTTPResponse();
        //
        try {
            //Parseando el objeto.
            JSONObject jsonObject = (JSONObject) parser.parse(request.getBody());

            //Ver la salida por la consola sobre la trama enviada por el APIGateway
            context.getLogger().log("" + jsonObject.toJSONString());

            //Recuperando el metodo de acceso de la llamada del API.
            if (jsonObject.get("requestContext") == null) {
                throw new IllegalArgumentException("No respesta el API de entrada");
            }

            String metodoHttp = request.getRequestContext().getHttp().getMethod();
            context.getLogger().log(String.format("METODO HTTP1 = %s, ", metodoHttp));

            switch (metodoHttp) {
                case "GET":
                    ReservaListResponse listarEstudiantesResponse = dynamoDBServices.listar(context);
                    reponse.setBody(gson.toJson(listarEstudiantesResponse));
                    break;
                case "POST":
                    reserva = jsonToReserva(jsonObject);
                    dynamoDBServices.insert(reserva, context);
                    reponse.setBody(gson.toJson(reserva));
                    break;
                case "PUT":
                    reserva = jsonToReserva(jsonObject);
                    dynamoDBServices.delete(reserva, context);
                    dynamoDBServices.insert(reserva, context);
                    reponse.setBody(gson.toJson(reserva));
                    break;
                case "DELETE":
                    reserva = jsonToReserva(jsonObject);
                    dynamoDBServices.delete(reserva, context);
                    reponse.setBody(gson.toJson(reserva));
                    break;
                default:
                    break;
            }

            JSONObject headerJson = new JSONObject();
            headerJson.put("mi-header", "Mi propio header");
            headerJson.put("Content-Type", "application/json");
            headerJson.put("Access-Control-Allow-Origin", "*");

            reponse.setHeaders(headerJson);


        } catch (ParseException ex){
            reponse.setStatusCode(400);
            reponse.setBody(ex.getMessage());
        }

        reponse.setStatusCode(200);
        return reponse;
    }

    private Reserva jsonToReserva(JSONObject json) throws IllegalArgumentException {
        if (json.get("body") == null) {
            throw new IllegalArgumentException("No envio el cuerpo en la trama.");
        }
        Reserva reserva = gson.fromJson(json.get("body").toString(), Reserva.class);
        return reserva;
    }
}
