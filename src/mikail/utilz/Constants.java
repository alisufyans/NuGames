package mikail.utilz;

public class Constants {
    public static class Player1Constants{
        public static final int PLAYER_1_UP = 0;
        public static final int PLAYER_1_DOWN = 1;
        public static final int PLAYER_1_LEFT = 2;
        public static final int PLAYER_1_RIGHT = 3;
        public static final int PLAYER_1_SAMOSA = 4;
        public static final int PLAYER_1_IDLE = 5;

        public static int GetPlayer1AnimationCol(final int action){
            switch (action){
                case PLAYER_1_UP:
                case PLAYER_1_DOWN:
                    return 0;
                case PLAYER_1_LEFT:
                case PLAYER_1_RIGHT:
                case PLAYER_1_SAMOSA:
                    return 1;
                case PLAYER_1_IDLE:
                    return 8;
                default:
                    return 8;
            }
        }

        public static int GetPlayer1AnimationRow(final int action){
            switch (action){
                case PLAYER_1_UP:
                case PLAYER_1_DOWN:
                case PLAYER_1_IDLE:
                    return 0;
                case PLAYER_1_RIGHT:
                    return 1;
                case PLAYER_1_LEFT:
                    return 2;
                case PLAYER_1_SAMOSA:
                    return 3;
                default:
                    return 0;
            }
        }
    }

    public static class Player2Constants{
        public static final int PLAYER_2_UP = 6;
        public static final int PLAYER_2_DOWN = 7;
        public static final int PLAYER_2_LEFT = 8;
        public static final int PLAYER_2_RIGHT = 9;
        public static final int PLAYER_2_SAMOSA = 10;
        public static final int PLAYER_2_IDLE = 11;

        public static int GetPlayer2AnimationRow(final int action){
            switch (action){
                case PLAYER_2_UP:
                case PLAYER_2_DOWN:
                case PLAYER_2_IDLE:
                    return 0;
                case PLAYER_2_RIGHT:
                    return 1;
                case PLAYER_2_LEFT:
                    return 2;
                case PLAYER_2_SAMOSA:
                    return 3;
                default:
                    return 0;
            }
        }

        public static int GetPlayer2AnimationCol(final int action){
            switch (action){
                case PLAYER_2_UP:
                case PLAYER_2_DOWN:
                    return 0;
                case PLAYER_2_LEFT:
                case PLAYER_2_RIGHT:
                case PLAYER_2_SAMOSA:
                    return 1;
                case PLAYER_2_IDLE:
                    return 8;
                default:
                    return 8;
            }
        }

    }
}
