package services;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import models.Reserva;
import models.ReservaListResponse;
import models.ReservaResponse;
import util.Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DynamoDBServices {

    public ReservaResponse insert(Reserva reserva, Context context) {
        AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();

        try {
            DynamoDBMapper mapper = new DynamoDBMapper(ddb);

            mapper.save(reserva);
        }catch (Exception e){
            return new ReservaResponse(reserva, true, e.getMessage());
        }

        return new ReservaResponse(reserva, false, null);
    }

    public ReservaListResponse listar(Context context) {
        AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();

        List<Reserva> reservas = new ArrayList<>();

        ScanRequest scanRequest = new ScanRequest().withTableName(Helper.getNombreTabla());
        ScanResult result = null;

        do{

            if (result != null) {
                scanRequest.setExclusiveStartKey(result.getLastEvaluatedKey());
            }
            result = ddb.scan(scanRequest);
            List<Map<String, AttributeValue>> rows = result.getItems();

            for (Map<String, AttributeValue> valueMap : rows) {
                System.out.println(""+valueMap);
                //
                AttributeValue id = valueMap.get("id");
                AttributeValue nombre = valueMap.get("nombre");
                AttributeValue lab = valueMap.get("lab");
                AttributeValue fecha = valueMap.get("fecha");
                AttributeValue horaInicio = valueMap.get("horaInicio");
                AttributeValue horaFin = valueMap.get("horaFin");
                AttributeValue carrera = valueMap.get("carrera");
                //
                Reserva tmp = new Reserva();

                tmp.setId(String.valueOf(id.getS()));

                if(nombre!=null){
                    tmp.setNombre(nombre.getN());
                }
                if(carrera!=null){
                    tmp.setCorreo(carrera.getS());
                }
                if(lab!=null){
                    tmp.setLab(lab.getS());
                }
                if(fecha!=null){
                    tmp.setFecha(fecha.getS());
                }
                if(horaInicio!=null){
                    tmp.setHoraInicio(horaInicio.getS());
                }
                if(horaFin!=null){
                    tmp.setHoraFin(horaFin.getS());
                }
                //
                reservas.add(tmp);
            }

        }while (result.getLastEvaluatedKey() != null);

        return new ReservaListResponse(reservas, false, null);
    }

    public ReservaResponse delete(Reserva reserva, Context context){
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable(Helper.getNombreTabla());

        DeleteItemOutcome outcome = table.deleteItem("id", reserva.getId());
        return new ReservaResponse(reserva, false, null);
    }
}
