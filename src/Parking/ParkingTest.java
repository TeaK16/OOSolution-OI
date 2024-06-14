package Parking;

import java.security.PublicKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

class Request{

    private static Integer globalID = 0;
    private Integer id;
    private  Integer parkingSpaceId;
    private  String  vehiclePlateNumber;
    private  Double  totalCostOfService;
    private Date dateOfReq;

    public Request(Integer parkingSpaceId, String vehiclePlateNumber, Double totalCostOfService, Date dateOfReq) {
        this.id  = globalID++;
        this.parkingSpaceId = parkingSpaceId;
        this.vehiclePlateNumber = vehiclePlateNumber;
        this.totalCostOfService = totalSum();
        this.dateOfReq = dateOfReq;
    }


    public Integer getId() {
        return id;
    }

    public Integer getParkingSpaceId() {
        return parkingSpaceId;
    }

    public String getVehiclePlateNumber() {
        return vehiclePlateNumber;
    }

    public Date getDateOfReq() {
        return dateOfReq;
    }

    public Double totalSum() {
        return 0.0;
    }

}

enum TypeParking{
    MONTH,
    YEAR
}

class PrepaidParking extends Request{
    private  TypeParking type;

    public PrepaidParking(Integer parkingSpaceId, String vehiclePlateNumber, Double totalCostOfService,Date dateOfReq, TypeParking type) {
        super(parkingSpaceId, vehiclePlateNumber, totalCostOfService, dateOfReq);
        this.type = type;
    }




    public TypeParking getType() {
        return type;
    }

    @Override
    public Double totalSum() {
        Double price = 0.0;
        if(this.type == TypeParking.MONTH){
            return 3000.0;
        } else if (this.type == TypeParking.YEAR) {
            return 36000.0;
        }

        return price;
    }

    public Double dailySum(TypeParking ty){
        Double dailyPrice =0.0;
        if (ty.equals(TypeParking.MONTH)) {
            dailyPrice = totalSum()/30;
        }else {
            dailyPrice = totalSum()/365;
        }

        return dailyPrice;
    }
}

class HourlyParking extends Request{
    private int numberOfHours;

    public HourlyParking(Integer parkingSpaceId, String vehiclePlateNumber, Double totalCostOfService,Date dateOfReq, int numberOfHours) {
        super(parkingSpaceId, vehiclePlateNumber, totalCostOfService,dateOfReq);
        this.numberOfHours = numberOfHours;
    }


    public int getNumberOfHours() {
        return numberOfHours;
    }

    @Override
    public Double totalSum() {
        return (double) (this.numberOfHours*35);
    }
}
class ParkingRequest{
   private static List<Request> requests;

    public ParkingRequest() {
        this.requests = new ArrayList<>();
    }

    public static List<Request> getRequests() {
        return requests;
    }

    public void createRequest(Request request){
        this.requests.add(request);
    }

    public void deleteRequest(Request request){
        this.requests.remove(request);
        System.out.println("The request with ID" + request.getId() + " is deleted");
    }

    public void getInfoForRequest(Request request){
        System.out.println("PARKING REQUEST INFO WITH ID:" + request.getId());
        System.out.println("Id of the parking space: "+ request.getParkingSpaceId());
        System.out.println("Plate number " + request.getVehiclePlateNumber());


        if(request instanceof PrepaidParking prepaid){
            System.out.println(prepaid.getType());
            System.out.println(prepaid.totalSum());
            System.out.println("_____________________");
        }

        if(request instanceof HourlyParking hour){
            System.out.println(hour.getNumberOfHours());
            System.out.println(hour.totalSum());
            System.out.println("_____________________");
        }
    }
}

class ParkingSpace{
    private Integer parkingSpaceId;
    private String availabilityStatus;
    private Double totalEarnings;

    public ParkingSpace(Integer parkingSpaceId) {
        this.parkingSpaceId = parkingSpaceId;
        this.availabilityStatus = checkAvailabilityOfParkingSpace();
        this.totalEarnings = 0.0;

    }

    public Integer getParkingSpaceId() {
        return parkingSpaceId;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public Double getTotalEarnings() {
        return totalEarnings;
    }

    public String checkAvailabilityOfParkingSpace(){
        String message = " ";
        List<Request> requests = ParkingRequest.getRequests();

        for (Request r : requests){
            if(Objects.equals(this.parkingSpaceId, r.getParkingSpaceId())){
                message =  "NOT AVAILABLE";
            }else{
                message = "AVAILABLE";
            }
        }

        return message;

    }

    public double totalEarningsForParkingSpace (Date date){
        List<Double> requestsWithID = new ArrayList<>();
        List<Request> requests = ParkingRequest.getRequests();

        for (Request r : requests) {

            if (r.getParkingSpaceId().equals(this.parkingSpaceId) && r.getDateOfReq().equals(date)) {
                if (r instanceof PrepaidParking) {
                    PrepaidParking req = (PrepaidParking) r;
                    req.dailySum(req.getType());
                    requestsWithID.add(r.totalSum());

                }else {
                    requestsWithID.add(r.totalSum());
                }
            }

        }
         this.totalEarnings = requestsWithID.stream().mapToDouble(Double::doubleValue).sum();
         return requestsWithID.stream().mapToDouble(Double::doubleValue).sum();
        }


}
public class ParkingTest {
    public static void main(String[] args) throws ParseException {
        String pattern = "dd-MM-yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date reqDate = format.parse("14-06-2024");
        HourlyParking parking = new HourlyParking(1,"SK4588JM", 0.0,reqDate,1 );
        PrepaidParking parking1 = new PrepaidParking(2,"BT2885MZ",0.0,reqDate,TypeParking.valueOf("MONTH"));
        PrepaidParking parking2 = new PrepaidParking(3,"OH3445ZZ",0.0,reqDate,TypeParking.valueOf("YEAR"));
        HourlyParking parking3 = new HourlyParking(1,"SK4588MM", 0.0,reqDate,1 );

        ParkingRequest request = new ParkingRequest();

        request.createRequest(parking);
        request.createRequest(parking1);
        request.createRequest(parking2);
        request.createRequest(parking3);

        request.getInfoForRequest(parking);
        request.getInfoForRequest(parking1);
        request.getInfoForRequest(parking2);

        request.deleteRequest(parking);
        request.deleteRequest(parking1);

        System.out.println("Number of request after delete " + request.getRequests().size());

        ParkingSpace space = new ParkingSpace(1);
        System.out.println("Parking space with id: "+ space.getParkingSpaceId()+" is " + space.getAvailabilityStatus());

        System.out.println(space.totalEarningsForParkingSpace(reqDate));

    }
}
