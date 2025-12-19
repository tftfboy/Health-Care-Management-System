# Healthcare Management System

A comprehensive Java Swing-based desktop application for managing healthcare clinic operations, including patient registration, doctor scheduling, appointments, medical records, and financial services.

## 📋 Features

### Core Functionality
- **Clinic Management**: Create and manage multiple clinics with unique IDs
- **Doctor Management**: Register doctors with specializations, licenses, and schedules
- **Patient Management**: Complete patient registration with medical history
- **Appointment Scheduling**: Book, reschedule, and track appointments
- **Financial Services**: Process payments, generate invoices, and track revenue
- **Medical Records**: Maintain health reports and medical documents
- **Statistics & Reports**: Comprehensive clinic analytics and reporting

### Advanced Features
- **Multi-Clinic Support**: Switch between different clinic instances
- **Live Search & Filtering**: Real-time search across doctors, patients, and appointments
- **Data Persistence**: Automatic saving and loading of clinic data
- **Modern UI**: Clean, intuitive interface with color-coded sections
- **Keyboard Shortcuts**: Quick navigation with hotkeys
- **Doctor Availability Management**: Track and manage doctor schedules
- **Payment Processing**: Support for multiple payment methods (Cash, Visa)
- **Trend Analysis**: Health report trends and vital sign tracking

## 🚀 Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Java Runtime Environment (JRE)

### Installation

1. Clone the repository:
```bash
git clone https://github.com/tftfboy/Health-Care-Management-System.git
```

2. Navigate to the project directory:
```bash
cd Health-Care-Management-System/Project12thf/Project12th/src
```

3. Compile the Java files:
```bash
javac *.java
```

4. Run the application:
```bash
java ClinicGUI
```

## 📁 Project Structure

```
OOP Project/
└── Project12thf/
    └── Project12th/
        ├── data/           # Data persistence files
        └── src/
            ├── ClinicGUI.java              # Main GUI interface
            ├── Clinic.java                 # Clinic management logic
            ├── Doctor.java                 # Doctor entity
            ├── Patient.java                # Patient entity
            ├── Person.java                 # Base person class
            ├── Appointment.java            # Appointment entity
            ├── AppointmentValidator.java   # Appointment validation
            ├── AppointmentCheckerImpl.java # Appointment checker
            ├── DoctorSchedule.java         # Doctor scheduling
            ├── FinancialService.java       # Financial operations
            ├── Health_Report.java          # Health reports
            ├── Medical_Document.java       # Medical documents
            ├── FileManager.java            # Data persistence
            └── IdGenerator.java            # Unique ID generation
```

## 🎯 Usage

### Creating a Clinic
1. Launch the application
2. Click "Create New Clinic"
3. Fill in clinic details (name, city, area, phone)
4. Click "Create Clinic"

### Managing Doctors
- **Add Doctor**: Register new doctors with specialization and license
- **View All**: Display all registered doctors
- **Search**: Find doctors by ID
- **Edit**: Update doctor information
- **Manage Availability**: Set doctor working days
- **Remove**: Delete doctor from system

### Managing Patients
- **Register Patient**: Add new patients with medical information
- **View All**: Display all registered patients
- **Search**: Find patients by ID
- **Edit**: Update patient information
- **Manage Records**: Add/view medical records
- **View Details**: See complete patient information

### Scheduling Appointments
- **Schedule**: Book new appointments with available doctors
- **View All**: See all scheduled appointments
- **Reschedule**: Change appointment date/time
- **Complete**: Mark appointments as completed
- **Cancel**: Cancel scheduled appointments

### Financial Management
- **Process Payments**: Handle patient payments
- **Payment History**: View all transactions
- **Edit Payments**: Modify payment details
- **Generate Reports**: Financial summaries and invoices

## ⌨️ Keyboard Shortcuts

- `F1` - Show Statistics
- `Ctrl+D` - Doctors Management
- `Ctrl+P` - Patients Management
- `Ctrl+A` - Appointments Management
- `Ctrl+H` - Return to Dashboard
- `Ctrl+S` - Save Current Clinic

## 💾 Data Persistence

The application automatically saves data to the `data/` directory:
- Clinic information
- Doctors and their schedules
- Patients and medical records
- Appointments
- Financial transactions

Data is loaded automatically on startup and saved on exit or when using Ctrl+S.

## 🎨 User Interface

The application features a modern, color-coded interface:
- **Blue**: Primary actions and navigation
- **Green**: Success states and confirmations
- **Orange**: Edit and modification actions
- **Red**: Delete and cancel operations
- **Purple**: Special features and reports

## 🔧 Technical Details

### Technologies Used
- Java Swing for GUI
- Java I/O for data persistence
- Object-oriented design patterns
- Event-driven architecture

### Key Classes
- **ClinicGUI**: Main interface and user interaction
- **Clinic**: Core business logic
- **FileManager**: Data persistence layer
- **AppointmentValidator**: Appointment validation logic

## 📊 Features in Detail

### Appointment Validation
- Prevents double-booking
- Validates doctor availability
- Checks for conflicting schedules
- Ensures proper time slots

### Financial Services
- Multiple payment methods
- Invoice generation
- Payment tracking
- Revenue reports
- Quick bill calculator

### Health Reports
- Vital signs tracking (BP, heart rate, temperature)
- Medical history
- Trend analysis
- Symptom logging

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📝 License

This project is open source and available for educational purposes.

## 👥 Authors

- **tftfboy** - Initial work

## 🙏 Acknowledgments

- Built as an Object-Oriented Programming (OOP) project
- Demonstrates real-world application of OOP principles
- Showcases Java Swing GUI development

## 📞 Support

For issues, questions, or suggestions, please open an issue on GitHub.

---

**Version**: 1.0  
**Last Updated**: December 2025
