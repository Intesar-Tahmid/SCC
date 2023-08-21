import java.util.*;

public class MainApplication {

    private static NotificationService notificationService = NotificationService.getInstance();
    private static Shop shop = new Shop();
    private static CommandInvoker commandInvoker = new CommandInvoker();
    private static int clientIdCounter = 0;
    private static Map<Integer, Client> clients = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continueRunning = true;

        while (continueRunning) {
            System.out.println("\nWelcome to SCC!");
            System.out.println("1. Buy a Car");
            System.out.println("2. Subscribe to Notifications");
            System.out.println("3. Request Car Servicing");
            System.out.println("4. Request Car Washing");
            System.out.println("5. Buy a Car Online");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear the buffer

            switch (choice) {
                case 1:
                    buyCar();
                    break;
                case 2:
                    subscribeToNotifications();
                    break;
                case 3:
                    requestCarServicing();
                    break;
                case 4:
                    requestCarWashing();
                    break;
                case 5:
                    buyCarOnline();
                    break;
                case 6:
                    System.out.println("Thank you for using SCC. Goodbye!");
                    continueRunning = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please choose again.");
                    break;
            }
        }
        scanner.close();
    }

    private static void buyCar() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nAvailable Cars: Ferrari, Ford");
        System.out.println("Enter the type of car you want to buy:");
        String type = scanner.nextLine();

        System.out.println("Do you want a RainShield? (yes/no)");
        String decision = scanner.nextLine();

        Car car = shop.buyCar(type);
        if ("yes".equalsIgnoreCase(decision)) {
            car = new RainShieldDecorator(car);
        }

        System.out.println("Bought a " + car.getClass().getSimpleName() + " for $" + car.getCost());
    }

    private static void subscribeToNotifications() {
        clientIdCounter++;
        Client client = new Client(clientIdCounter);
        notificationService.subscribe(client);
        clients.put(clientIdCounter, client);
        System.out.println("Client with ID: " + clientIdCounter + " subscribed.");
    }

    private static void requestCarServicing() {
        Command serviceCommand = new ServiceCarCommand();
        commandInvoker.execute(serviceCommand);
    }

    private static void requestCarWashing() {
        Command washCommand = new WashCarCommand();
        commandInvoker.execute(washCommand);
    }

    private static void buyCarOnline() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nAvailable Cars: Ferrari, Ford");
        System.out.println("Enter the type of car you want to buy:");
        String type = scanner.nextLine();

        Car car = shop.buyCarOnline(type);
        System.out.println("Bought a " + car.getClass().getSimpleName() + " online for $" + car.getCost());
    }
}

// Placeholder classes filled out

class Car {
    protected double cost;
    public double getCost() { return cost; }
}

class CarFactory {
    public Car createCar(String type) {
        switch(type) {
            case "Ferrari":
                return new FerrariCar();
            // Add cases for other car types
            default:
                return null;
        }
    }
}

class FerrariCar extends Car {
    public FerrariCar() {
        this.cost = 200000; // Arbitrary number
    }
}

class CarDecorator extends Car {
    protected Car car;
    public CarDecorator(Car car) { this.car = car; }
    @Override
    public double getCost() { return car.getCost(); }
}

class RainShieldDecorator extends CarDecorator {
    public RainShieldDecorator(Car car) {
        super(car);
    }

    @Override
    public double getCost() {
        return car.getCost() + 500; // Arbitrary added cost
    }
}

// Singleton NotificationService
class NotificationService {
    private List<Client> clients = new ArrayList<>();

    private static NotificationService instance;

    private NotificationService() {}

    public static NotificationService getInstance() {
        if(instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    public void subscribe(Client client) {
        clients.add(client);
    }

    public void unsubscribe(Client client) {
        clients.remove(client);
    }

    public void notifyAllClients() {
        for(Client client : clients) {
            client.update();
        }
    }
}

class Client {
    private int id;
    public Client(int id) {
        this.id = id;
    }

    public void update() {
        System.out.println("Client " + id + " received a notification.");
    }
}

interface Command {
    void execute();
}

class ServiceCarCommand implements Command {
    @Override
    public void execute() {
        System.out.println("Car is being serviced.");
    }
}

class WashCarCommand implements Command {
    @Override
    public void execute() {
        System.out.println("Car is being washed.");
    }
}

class CommandInvoker {
    public void execute(Command command) {
        command.execute();
    }
}

class Shop {
    private CarFactory factory = new CarFactory();

    public Car buyCar(String type) {
        return factory.createCar(type);
    }

    // Assuming buyCarOnline just notifies the client about an online purchase
    public Car buyCarOnline(String type) {
        Car car = buyCar(type);
        NotificationService.getInstance().notifyAllClients();
        return car;
    }
}
