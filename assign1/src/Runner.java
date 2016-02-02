public class Runner {
    public static void main(String args[]) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Host host = new Host("localhost", 6868, 6969);
                host.run();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Server server = new Server("localhost", 6969);
                try {
                    server.run();
                } catch (InvalidRequestException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Client client = new Client("localhost", 6767, 6868);
                client.run();
            }
        }).start();

    }
}
