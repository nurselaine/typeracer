//package Server.Server_RPC;
//
//import Server.Server_context.UserContext;
//
//import java.time.Duration;
//import java.time.Instant;
//
//public class GameTimerRPC {
//
//    UserContext user;
//
//    Instant startTime;
//
//    Instant endTime;
//    long totalTime;
//
//    long timeout = 60; // 1 minutes max time
//    /**
//     * ?? Create a new class to track game times for each user and calculate
//     * game placement (1st, 2nd, 3rd, 4th) in that class ??
//     *
//     * ?? User will send RPC with total time taken to complete typing to new class
//     * ?? new class will hold all user times and calculate avg time speed and scores
//     *
//     * ?? Game context will use this new class to send users result of game
//     * */
//    public GameTimerRPC(UserContext user){
//        this.user = user;
//        this.startTime = Instant.now();
//        this.totalTime = timeout; // this will be default for users that do not finish typing
//    }
//
//    // checks if player still has time to complete typing
//    // or if game should end
//    public boolean timeChecker(){
//        Instant currentTime = Instant.now();
//        long cumulativeTime = Duration.between(currentTime, startTime).getSeconds();
//        return cumulativeTime < timeout;
//    }
//
//    public void gameEnd(UserContext user){
//        this.endTime = Instant.now();
//        this.totalTime = calculatePlayerTime();
//        user.endGame(totalTime);
//    }
//
//    // Checks player time
//    public long calculatePlayerTime(){
//        return Duration.between(this.startTime, this.endTime).getSeconds();
//    }
//}
