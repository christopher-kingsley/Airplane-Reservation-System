import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.stream.IntStream;

class Plane {
    private int[] businessSeats;
    private int[] economySeats;
    private String destination;
    private int availableBusinessSeats;
    private int availableEconomySeats;

    public Plane(int businessSeatCount, int economySeatCount, String destination) {
        this.businessSeats = IntStream.rangeClosed(1, businessSeatCount).toArray();
        this.economySeats = IntStream.rangeClosed(1, economySeatCount).toArray();
        this.destination = destination;
        this.availableBusinessSeats = businessSeatCount;
        this.availableEconomySeats = economySeatCount;
    }

    public String getDestination() {
        return destination;
    }

    public int getAvailableBusinessSeats() {
        return availableBusinessSeats;
    }

    public int getAvailableEconomySeats() {
        return availableEconomySeats;
    }

    public String reserveTicket(String type, int seatNumber, String snack, String date, String time) {
        if (type.equalsIgnoreCase("business")) {
            if (seatNumber < 1 || seatNumber > businessSeats.length || businessSeats[seatNumber - 1] == -1) {
                return "Business seat " + seatNumber + " is already taken.";
            }
            businessSeats[seatNumber - 1] = -1;
            availableBusinessSeats--;
            return "Business seat " + seatNumber + " reserved on " + date + " at " + time + " with " + snack + ". Total: $" + calculatePrice("business");
        } else if (type.equalsIgnoreCase("economy")) {
            if (seatNumber < 1 || seatNumber > economySeats.length || economySeats[seatNumber - 1] == -1) {
                return "Economy seat " + seatNumber + " is already taken.";
            }
            economySeats[seatNumber - 1] = -1;
            availableEconomySeats--;
            return "Economy seat " + seatNumber + " reserved on " + date + " at " + time + ". Total: $" + calculatePrice("economy");
        }
        return "Invalid class type.";
    }

    public int calculatePrice(String type) {
        if (type.equalsIgnoreCase("business")) {
            return 600;
        } else {
            return 400;
        }
    }

    public void displaySeatInfo() {
        System.out.println("Destination: " + destination);
        System.out.println("Business Seats Available: " + availableBusinessSeats + " " +
                Arrays.toString(Arrays.stream(businessSeats).filter(seat -> seat != -1).toArray()));
        System.out.println("Economy Seats Available: " + availableEconomySeats + " " +
                Arrays.toString(Arrays.stream(economySeats).filter(seat -> seat != -1).toArray()));
    }
}

public class ReservationSystem extends JFrame {
    private JTextField customerNameField;
    private JComboBox<String> flightNumberBox, classBox, seatBox, snackBox, dateBox, timeBox;
    private JTextPane outputPane;
    private Plane flightToNY;
    private Plane flightToGreensboro;

    public ReservationSystem() {
        setTitle("Greensboro Airlines Reservation System");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridLayout(9, 2, 5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        customerNameField = new JTextField(10);
        String[] flightNumbers = {"GOS 1 - Greensboro to Newark", "GOS 2 - Newark to Greensboro"};
        flightNumberBox = new JComboBox<>(flightNumbers);

        String[] classes = {"Economy", "Business"};
        classBox = new JComboBox<>(classes);

        snackBox = new JComboBox<>(new String[]{"None", "Cookies", "Peanuts"});

        seatBox = new JComboBox<>();
        for (int i = 1; i <= 20; i++) {
            seatBox.addItem(String.valueOf(i));
        }

        dateBox = new JComboBox<>(generateNext7Days());
        timeBox = new JComboBox<>(generateAvailableHours());

        JButton reserveButton = new JButton("Reserve Ticket");
        
        outputPane = new JTextPane();
        outputPane.setEditable(false);
        outputPane.setFont(new Font("Arial", Font.BOLD, 16));
        outputPane.setBackground(new Color(240, 240, 240));
        outputPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        outputPane.setPreferredSize(new Dimension(250, 150));

        mainPanel.add(new JLabel("Customer Name:"));
        mainPanel.add(customerNameField);
        mainPanel.add(new JLabel("Flight Number:"));
        mainPanel.add(flightNumberBox);
        mainPanel.add(new JLabel("Class:"));
        mainPanel.add(classBox);
        mainPanel.add(new JLabel("Seat Number:"));
        mainPanel.add(seatBox);
        mainPanel.add(new JLabel("Snack (Business Only):"));
        mainPanel.add(snackBox);
        mainPanel.add(new JLabel("Date:"));
        mainPanel.add(dateBox);
        mainPanel.add(new JLabel("Time:"));
        mainPanel.add(timeBox);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(reserveButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(new JScrollPane(outputPane), BorderLayout.EAST);

        flightToNY = new Plane(20, 10, "New York");
        flightToGreensboro = new Plane(20, 10, "Greensboro");

        reserveButton.addActionListener(new ReserveTicketHandler());

        setVisible(true);
    }

    private String[] generateNext7Days() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String[] dates = new String[7];
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 7; i++) {
            dates[i] = dateFormat.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dates;
    }

    private String[] generateAvailableHours() {
        String[] hours = new String[16];
        int startHour = 6;

        for (int i = 0; i < hours.length; i++) {
            hours[i] = String.format("%02d:00 %s", (startHour > 12 ? startHour - 12 : startHour), (startHour < 12 ? "AM" : "PM"));
            startHour++;
        }
        return hours;
    }

    private class ReserveTicketHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String customerName = customerNameField.getText();
            String flightNumber = (String) flightNumberBox.getSelectedItem();
            String ticketClass = (String) classBox.getSelectedItem();
            String snack = (String) snackBox.getSelectedItem();
            int seatNumber = Integer.parseInt((String) seatBox.getSelectedItem());
            String date = (String) dateBox.getSelectedItem();
            String time = (String) timeBox.getSelectedItem();

            if (ticketClass.equals("Economy") && !snack.equals("None")) {
                JOptionPane.showMessageDialog(ReservationSystem.this, 
                    "Error: Snacks are only available for Business class tickets.", 
                    "Snack Selection Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            Plane selectedPlane = flightNumber.contains("GOS 1") ? flightToNY : flightToGreensboro;

            String reservationResult = selectedPlane.reserveTicket(ticketClass, seatNumber, snack, date, time);

            outputPane.setText("Reservation for " + customerName + ":\n" + reservationResult);
            selectedPlane.displaySeatInfo();
        }
    }

    public static void main(String[] args) {
        new ReservationSystem();
    }
}
