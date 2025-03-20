package org.example;

import org.example.entities.Train;
import org.example.entities.User;
import org.example.services.UserBookingService;
import org.example.util.UserServiceUtil;

import java.util.*;

public class App {

    public static void main(String[] args) {
        System.out.println("Running Train Ticket Booking System");
        Scanner scanner = new Scanner(System.in);
        int choice = 0;
        UserBookingService userBookingService = null;

        // Initialize UserBookingService with error handling
        try {
            userBookingService = new UserBookingService();
            System.out.println("UserBookingService initialized successfully.");
        } catch (Exception e) {
            System.err.println("Error: Failed to initialize UserBookingService.");
            e.printStackTrace();  // Prints the actual error
            return;
        }

        while (choice != 7) {
            System.out.println("\nMenu:");
            System.out.println("1. Register User");
            System.out.println("2. Login User");
            System.out.println("3. Fetch Bookings");
            System.out.println("4. Search Trains");
            System.out.println("5. Book a Seat");
            System.out.println("6. Cancel My Ticket");
            System.out.println("7. Exit the App");
            System.out.print("Enter your choice: ");

            // Handle invalid input
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.next(); // Clear invalid input
                continue;
            }

            choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline

            Train trainSelectedForBooking = new Train();

            switch (choice) {
                case 1:
                    System.out.print("Enter The Username To Sign Up: ");
                    String nameToSignUp = scanner.next();
                    System.out.print("Enter The Password To Sign Up: ");
                    String passwordToSignUp = scanner.next();

                    User userToSignUp = new User(
                            nameToSignUp,
                            passwordToSignUp,
                            UserServiceUtil.hashPassword(passwordToSignUp),
                            new ArrayList<>(),
                            UUID.randomUUID().toString()
                    );

                    if (userBookingService.signUp(userToSignUp)) {
                        System.out.println("User registered successfully!");
                    } else {
                        System.out.println("Error: Registration failed.");
                    }
                    break;

                case 2:
                    System.out.print("Enter The Username To Login: ");
                    String nameToLogin = scanner.next();
                    System.out.print("Enter The Password To Login: ");
                    String passwordToLogin = scanner.next();

                    User userToLogin = new User(
                            nameToLogin,
                            passwordToLogin,
                            UserServiceUtil.hashPassword(passwordToLogin),
                            new ArrayList<>(),
                            UUID.randomUUID().toString()
                    );

                    try {
                        userBookingService = new UserBookingService(userToLogin);
                        System.out.println("Login successful!");
                    } catch (Exception e) {
                        System.err.println("Error: Login failed. Please try again.");
                        e.printStackTrace();
                    }
                    break;

                case 3:
                    System.out.println("Fetching your bookings...");
                    userBookingService.fetchBookings();
                    break;

                case 4:
                    System.out.print("Type your source station: ");
                    String source = scanner.next();
                    System.out.print("Type your destination station: ");
                    String dest = scanner.next();

                    List<Train> trains = userBookingService.getTrains(source, dest);
                    if (trains.isEmpty()) {
                        System.out.println("No trains found for the given route.");
                    } else {
                        int index = 1;
                        for (Train t : trains) {
                            System.out.println(index + ". Train ID: " + t.getTrainId());
                            for (Map.Entry<String, String> entry : t.getStationTimes().entrySet()) {
                                System.out.println("Station: " + entry.getKey() + " Time: " + entry.getValue());
                            }
                            index++;
                        }
                        System.out.print("Select a train by typing the number: ");
                        int selectedIndex = scanner.nextInt();
                        if (selectedIndex > 0 && selectedIndex <= trains.size()) {
                            trainSelectedForBooking = trains.get(selectedIndex - 1);
                        } else {
                            System.out.println("Invalid selection.");
                        }
                    }
                    break;

                case 5:
                    System.out.println("Select a seat from the following seats:");
                    List<List<Integer>> seats = userBookingService.fetchSeats(trainSelectedForBooking);
                    if (seats.isEmpty()) {
                        System.out.println("No seats available.");
                        break;
                    }

                    for (List<Integer> row : seats) {
                        for (Integer val : row) {
                            System.out.print(val + " ");
                        }
                        System.out.println();
                    }

                    System.out.print("Enter the row: ");
                    int row = scanner.nextInt();
                    System.out.print("Enter the column: ");
                    int col = scanner.nextInt();

                    System.out.println("Booking your seat...");
                    Boolean booked = userBookingService.bookTrainSeat(trainSelectedForBooking, row, col);

                    if (booked) {
                        System.out.println("Booking successful! Enjoy your journey.");
                    } else {
                        System.out.println("Error: Unable to book the seat.");
                    }
                    break;

                case 6:
                    System.out.print("Enter ticket ID to cancel: ");
                    String ticketId = scanner.next();
                    if (userBookingService.cancelBooking(ticketId)) {
                        System.out.println("Ticket canceled successfully.");
                    } else {
                        System.out.println("Error: Ticket not found.");
                    }
                    break;

                case 7:
                    System.out.println("Exiting the application. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid choice! Please select a valid option.");
                    break;
            }
        }
        scanner.close(); // Close the scanner to prevent memory leaks
    }
}
