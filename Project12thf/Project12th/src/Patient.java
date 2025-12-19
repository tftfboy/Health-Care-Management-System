import java.util.ArrayList;

public class Patient extends Person {
    private int age;
    private String gender;
    private String bloodType;
    private String contactInfo;
    private ArrayList<String> MedicalRecords = new ArrayList<>();
    
    public Patient(int age, String address, String phone, String name, String bloodType, String contactInfo, String gender, ArrayList<String> MedicalRecords) {
        super(IdGenerator.generatePatientId(), name, address, phone);
        this.age = age;
        this.bloodType = bloodType;
        this.contactInfo = contactInfo;
        this.gender = gender;
        this.MedicalRecords = MedicalRecords;

    }
    public void setAge(int age) {
        if(age > 0)
            this.age = age;
        else
            System.out.println("Invalid age please enter it correctly!");
    }

    public void setBloodType(String bloodType) {
        bloodType = bloodType.toUpperCase();
        if(bloodType.equals("O+") || bloodType.equals("O-") || 
           bloodType.equals("A+") || bloodType.equals("A-") || 
           bloodType.equals("B+") || bloodType.equals("B-") || 
           bloodType.equals("AB+") || bloodType.equals("AB-")) {
            this.bloodType = bloodType;
        }
        else {
            System.out.println("Invalid blood type! Please use: A+, A-, B+, B-, AB+, AB-, O+, O-");
        }
    }

    public void setGender(String gender) {
        System.out.println("ENTER (M) if Male and (F) if Female ");
        gender = gender.toUpperCase();
        if(gender.equals("M") || gender.equals("F"))
            this.gender = gender;
        else
            System.out.println("INVALID!");
    }

    public void setMedicalRecords(ArrayList<String> MedicalRecords) {
        this.MedicalRecords = MedicalRecords;
    }

    public int getAge() {
        return age;
    }

    public String getBloodType() {
        return bloodType;
    }

    public String getGender() {
        return gender;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void updateContactInfo(String newcontactInfo) {
        this.contactInfo = newcontactInfo;
        System.out.println("You have updated your contactInfo sucessfuly!!");
    }//new contact is not an attribute to be defined..

    public void addMedicalRecord(String record) {
        MedicalRecords.add(record);// Adding is a feature in arraylist..
        System.out.println("New record added successfully!");
    }

    public ArrayList<String> getMedicalRecords() {
        System.out.println("Medical Records:");
        for (int i = 0; i < MedicalRecords.size(); i++) {
            System.out.println("-----" + MedicalRecords.get(i));
        }
        return MedicalRecords;
    }
    public void displayPatientInfo() {
        System.out.println(super.displayInfo()); // prints id, name, address, phone
        System.out.println("Age: " + age);
        System.out.println("Gender: " + gender);
        System.out.println("Blood Type: " + bloodType);
        getMedicalRecords();
    }
}