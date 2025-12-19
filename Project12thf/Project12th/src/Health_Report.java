import java.util.ArrayList;
import java.util.Date;
import java.io.Serializable;

public class Health_Report implements Serializable {
    private String reportId;
    private Patient patient;
    private Date reportDate;
    private double height;
    private double weight;
    private String bloodPressure;
    private double bmi;
    private int heartRate;
    private double temperature;
    private String bloodSugar;
    private String generalCondition;
    private ArrayList<String> healthNotes;
    public Health_Report(Patient patient, double height, double weight, String bloodPressure, double bmi, int heartRate, double temperature, String bloodSugar) {
        this.reportId = IdGenerator.generateHealthReportId();
        this.patient = patient;
        this.reportDate = new Date();
        this.height = height;
        this.weight = weight;
        this.bloodPressure = bloodPressure;
        this.bmi = bmi;
        this.heartRate = heartRate;
        this.temperature = temperature;
        this.bloodSugar = bloodSugar;
        this.generalCondition = "Normal";
        this.healthNotes = new ArrayList<>();
    }

    // Getters
    public String getReportId() {
        return reportId;
    }

    public Patient getPatient() {
        return patient;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public double getBmi() {
        return bmi;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getBloodSugar() {
        return bloodSugar;
    }

    public ArrayList<String> getHealthNotes() {
        return healthNotes;
    }

    // Setters with Validation

    public void setReportId(String reportId) {
            this.reportId = reportId;
    }



    public void setReportDate(Date reportDate) {
        if (reportDate != null) {
            this.reportDate = reportDate;
        } else {
            System.out.println("Error: Report date cannot be null!");
        }
    }

    public void setHeight(double height) {
        if (height > 0 && height <= 300) {
            this.height = height;
        } else {
            System.out.println("Error: Invalid height! Height must be between 0 and 300 cm.");
        }
    }

    public void setWeight(double weight) {
        if (weight > 0 && weight <= 500) {
            this.weight = weight;
        } else {
            System.out.println("Error: Invalid weight! Weight must be between 0 and 500 kg.");
        }
    }

    public void setBloodPressure(String bloodPressure) {
        if (bloodPressure != null && bloodPressure.matches("\\d{2,3}/\\d{2,3}")) {
            this.bloodPressure = bloodPressure;
        } else {
            System.out.println("Error: Invalid blood pressure format! Use format: XXX/XX (e.g., 120/80)");
        }
    }

    public void setBmi(double bmi) {
        if (bmi > 0 && bmi <= 100) {
            this.bmi = bmi;
        } else {
            System.out.println("Error: Invalid BMI! BMI must be between 0 and 100.");
        }
    }

    public void setHeartRate(int heartRate) {
        if (heartRate >= 40 && heartRate <= 200) {
            this.heartRate = heartRate;
        } else {
            System.out.println("Error: Invalid heart rate! Heart rate must be between 40 and 200 bpm.");
        }
    }

    public void setTemperature(double temperature) {
        if (temperature >= 35.0 && temperature <= 42.0) {
            this.temperature = temperature;
        } else {
            System.out.println("Error: Invalid temperature! Temperature must be between 35°C and 42°C.");
        }
    }

    public void setBloodSugar(String bloodSugar) {
                this.bloodSugar = bloodSugar;
            }

    public void setGeneralCondition(String generalCondition) {
        if (generalCondition != null && !generalCondition.isEmpty()) {
            this.generalCondition = generalCondition;
        } else {
            System.out.println("Error: General condition cannot be empty!");
        }
    }

    public String getGeneralCondition() {
        return generalCondition;
    }

    // Utility Methods
    public void addHealthNote(String note) {
            healthNotes.add(note);
            System.out.println("Health note added successfully!");
    }        

    public double calculateBMI(double weight, double height) {
        if (height <= 0 || weight <= 0) {
            System.out.println("Error: Height and weight must be positive values!");
            return 0;
        }
        double heightInMeters = height / 100.0;
        double calculatedBMI = weight / (heightInMeters * heightInMeters);
        this.bmi = calculatedBMI;
        return calculatedBMI;
    }

    public void trackHealthTrends() {
        System.out.println("\n===== HEALTH TRENDS =====\n");
        
        // BMI Analysis
        if (bmi < 18.5) {
            System.out.println("BMI Status: Underweight (" + String.format("%.2f", bmi) + ")");
        } else if (bmi >= 18.5 && bmi < 25) {
            System.out.println("BMI Status: Normal weight (" + String.format("%.2f", bmi) + ")");
        } else if (bmi >= 25 && bmi < 30) {
            System.out.println("BMI Status: Overweight (" + String.format("%.2f", bmi) + ")");
        } else {
            System.out.println("BMI Status: Obese (" + String.format("%.2f", bmi) + ")");
        }
        
        // Heart Rate Analysis
        if (heartRate < 60) {
            System.out.println("Heart Rate: Low (" + heartRate + " bpm) - Consider medical checkup");
        } else if (heartRate >= 60 && heartRate <= 100) {
            System.out.println("Heart Rate: Normal (" + heartRate + " bpm)");
        } else {
            System.out.println("Heart Rate: High (" + heartRate + " bpm) - Consider medical checkup");
        }
        
        // Temperature Analysis
        if (temperature < 36.5) {
            System.out.println("Temperature: Low (" + temperature + "°C) - Hypothermia risk");
        } else if (temperature >= 36.5 && temperature <= 37.5) {
            System.out.println("Temperature: Normal (" + temperature + "°C)");
        } else if (temperature > 37.5 && temperature <= 38.5) {
            System.out.println("Temperature: Slight Fever (" + temperature + "°C)");
        } else {
            System.out.println("Temperature: High Fever (" + temperature + "°C) - Medical attention needed");
        }
        
        // Blood Pressure Status
        System.out.println("Blood Pressure: " + bloodPressure);
        System.out.println("Blood Sugar: " + bloodSugar);
        System.out.println("Overall Condition: " + generalCondition);
        System.out.println("========================\n");
    }

    public String getHealthDetails() {
        return "Health_Report{" +
                "reportId='" + reportId + '\'' +
                ", patient=" + (patient != null ? patient.getName() : "null") +
                ", height=" + height +
                ", weight=" + weight +
                ", bmi=" + String.format("%.2f", bmi) +
                ", bloodPressure='" + bloodPressure + '\'' +
                ", heartRate=" + heartRate +
                ", temperature=" + temperature +
                ", bloodSugar='" + bloodSugar + '\'' +
                ", notesCount=" + healthNotes.size() +
                '}';
    }
}
