public class Deadlock {
    public static void main(String[] args) throws InterruptedException {
        class Account {
            int balance = 100;
            public Account(int balance) { this.balance = balance; }
            public synchronized void deposit(int amount) { balance += amount; }
            public synchronized boolean withdraw(int amount) {
                if (balance >= amount) {
                    balance -= amount;
                    return true;
                }
                return false;
            }
            public synchronized boolean transfer(Account destination, int amount) {
                if (balance >= amount) {
                    balance -= amount;
                    synchronized(destination) {
                        destination.balance += amount;
                    }
                    return true;
                }
                return false;
            }
            public synchronized int getBalance() { return balance; }
        }

        final Account bob = new Account(200000);
        final Account joe = new Account(300000);

        class FirstTransfer extends Thread {
            public void run() {
                for (int i = 0; i < 100000; i++) {
                    bob.transfer(joe, 2);
                }
            }
        }
        class SecondTransfer extends Thread {
            public void run() {
                for (int i = 0; i < 100000; i++) {
                    joe.transfer(bob, 1);
                }
            }
        }

        FirstTransfer thread1 = new FirstTransfer();
        SecondTransfer thread2 = new SecondTransfer();
        thread1.start(); thread2.start();
        thread1.join(); thread2.join();
        System.out.println("Bob's balance: " + bob.getBalance());
        System.out.println("Joe's balance: " + joe.getBalance());
    }
}
