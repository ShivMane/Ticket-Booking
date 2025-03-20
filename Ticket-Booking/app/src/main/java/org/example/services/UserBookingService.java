package org.example.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
//import jdk.dynalink.linker.LinkerServices;
import org.example.entities.Train;
import org.example.entities.User;
import org.example.util.UserServiceUtil;

import javax.imageio.IIOException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UserBookingService {

    private ObjectMapper objectMapper = new ObjectMapper();

    private List<User> userList;

    private User user;

    private final String USER_FILE_PATH = "app/src/main/java/org/example/localDB/users.json";

    public UserBookingService(User user) throws IOException {
        this.user = user;
        loadUserListFromFile();
    }

    public UserBookingService() throws IOException {
        loadUserListFromFile();
    }

    private void loadUserListFromFile() throws IOException {
        File file = new File(USER_FILE_PATH);
        if (!file.exists()) {
            System.out.println("Warning: users.json file not found. Creating a new file.");
            userList = new ArrayList<>(); // Initialize empty list
            saveUserListToFile(); // Save an empty list to avoid future errors
            return;
        }
        try {
            userList = objectMapper.readValue(file, new TypeReference<List<User>>() {
            });
        } catch (IOException e) {
            System.out.println("Error reading users.json: " + e.getMessage());
            userList = new ArrayList<>();
        }
    }


    public Boolean loginUser() {
        Optional<User> foundUser = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        return foundUser.isPresent();
    }

    public Boolean signUp(User user1) {
        try {
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        } catch (IOException ex) {
            return Boolean.FALSE;
        }
    }

    private void saveUserListToFile() throws IOException {
        File usersFile = new File(USER_FILE_PATH);
        objectMapper.writeValue(usersFile, userList);
    }

    public void fetchBookings() {
        Optional<User> userFetched = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        if (userFetched.isPresent()) {
            userFetched.get().printTickets();
        }
    }

    // todo: Complete this function
    public Boolean cancelBooking(String ticketId) {

        Scanner s = new Scanner(System.in);
        System.out.println("Enter the ticket id to cancel");
        ticketId = s.next();

        if (ticketId == null || ticketId.isEmpty()) {
            System.out.println("Ticket ID cannot be null or empty.");
            return Boolean.FALSE;
        }

        String finalTicketId1 = ticketId;  //Because strings are immutable
        boolean removed = user.getTicketsBooked().removeIf(ticket -> ticket.getTicketId().equals(finalTicketId1));

        String finalTicketId = ticketId;
        user.getTicketsBooked().removeIf(Ticket -> Ticket.getTicketId().equals(finalTicketId));
        if (removed) {
            System.out.println("Ticket with ID " + ticketId + " has been canceled.");
            return Boolean.TRUE;
        } else {
            System.out.println("No ticket found with ID " + ticketId);
            return Boolean.FALSE;
        }
    }


    public List<Train> getTrains(String source, String destination) {
        TrainService trainService = new TrainService();  // ✅ No IOException needed
        return trainService.searchTrains(source, destination);
    }

    public List<List<Integer>> fetchSeats(Train train) {
        return train.getSeats();
    }

    public Boolean bookTrainSeat(Train train, int row, int seat) {
        TrainService trainService = new TrainService();  // ✅ No IOException needed
        List<List<Integer>> seats = train.getSeats();

        if (row >= 0 && row < seats.size() && seat >= 0 && seat < seats.get(row).size()) {
            if (seats.get(row).get(seat) == 0) {
                seats.get(row).set(seat, 1);
                train.setSeats(seats);
                trainService.addTrain(train);
                return true; // Booking successful
            } else {
                return false; // Seat is already booked
            }
        } else {
            return false; // Invalid row or seat index
        }
    }
}
