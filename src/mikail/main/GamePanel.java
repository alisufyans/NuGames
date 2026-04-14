package mikail.main;

import mikail.inputs.KeyboardInputs;
import mikail.inputs.MouseInputs;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static mikail.utilz.Constants.Player1Constants.*;
import static mikail.utilz.Constants.Player2Constants.*;

public class GamePanel extends JPanel {
    //Game
    public boolean RUNNING = false;
    private final int WIDTH = 1200;
    private final int HEIGHT = 700;
    private BufferedImage startImg;
    private BufferedImage backImg;
    private BufferedImage win1Img;
    private BufferedImage win2Img;
    // Player 1
    private final float  XSTARTINGPOS = 74;
    private final float  YSTARTINGPOS = 245;
    private float xDelta = XSTARTINGPOS;
    private float yDelta = YSTARTINGPOS;
    private int aniTick = 0, aniIndex = 0, aniSpeed = 150;
    private int charAnimationRow;
    private int charAnimationCol;
    private BufferedImage[][] player1Imgs;
    private int player_1_samosas = 0;
    private boolean player1_has_samosa = false;
    private boolean player_1_diamond = false;


    // Player 2
    private final float  XSTARTINGPOS2 = 74;
    private final float  YSTARTINGPOS2 = 400;
    private float xDelta2 = XSTARTINGPOS2;
    private float yDelta2 = YSTARTINGPOS2;
    private int aniTick2 = 0, aniIndex2 = 0, aniSpeed2 = 150;
    private int charAnimationRow2;
    private int charAnimationCol2;
    private BufferedImage[][] player2Imgs;
    private int player_2_samosas = 0;
    private boolean player2_has_samosa = false;
    private boolean player_2_diamond = false;


    // Both Players
    private BufferedImage charImg;


    // Collectables
    private BufferedImage collectableImg;
    private BufferedImage[][] collectableImgs;
    private int aniTickCol = 0, aniIndexCol = 0, aniSpeedCol = 60;
    private int maxDiamonds = 5;
    private boolean samosa_1 = true, samosa_2 = true, samosa_3 = true, samosa_4 = true, samosa_5 = true;


    // Both Lines Cars
    private final int CAR_Y_DELTA = 700;
    private final int CAR_Y_DELTAOPPOSITE = -100;


    //First and Second Line Car
    private BufferedImage carImg;
    private BufferedImage[][] carImgs;
    private int aniTickCar = 0, aniSpeedCar = 10;
    private int carXDeltaLine1 = 250;
    private int carXDeltaLine2 = 400;
    private int carYDelta = CAR_Y_DELTA;
    private int carYDelta2 = CAR_Y_DELTA;


    // Third and Fourth Line cars
    private BufferedImage carImg2;
    private BufferedImage[][] carImgs2;
    private int carXDeltaLine3 = 710;
    private int carXDeltaLine4 = 860;
    private int carYDeltaOpposite = CAR_Y_DELTAOPPOSITE;
    private int carYDeltaOpposite2 = CAR_Y_DELTAOPPOSITE;
    private int game_start = 0;
    private int currentSamosas;


    public GamePanel() {
        setDimensions();
        importImgs();
        addKeyListener(new KeyboardInputs(this));
        addMouseListener(new MouseInputs());
        loadPlayersAnimations();
        loadCollectablesAnimation();
        loadCarAnimations();
    }

    private void setDimensions() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

    private void loadPlayersAnimations() {
        player1Imgs = new BufferedImage[4][8];
        player2Imgs = new BufferedImage[4][8];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                player1Imgs[i][j] = charImg.getSubimage(j * 120, i * 120, 120, 120);
                player2Imgs[i][j] = charImg.getSubimage(j * 120, (4 + i) * 120, 120, 120);
            }
        }
    }

    private void loadCollectablesAnimation() {
        collectableImgs = new BufferedImage[2][9];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 9; j++) {
                collectableImgs[i][j] = collectableImg.getSubimage(j * 120, i * 120, 120, 120);
            }
        }
    }

    private void loadCarAnimations() {
        carImgs = new BufferedImage[9][2];
        carImgs2 = new BufferedImage[9][2];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 2; j++) {
                carImgs[i][j] = carImg.getSubimage(j * 120, i * 120, 120, 120);
                carImgs2[i][j] = carImg2.getSubimage(j * 120, i * 120, 120, 120);
            }
        }
    }


    private void importImgs() {
        InputStream characterIS = getClass().getResourceAsStream("/characters.png");
        InputStream backIS = getClass().getResourceAsStream("/background.png");
        InputStream collectablesIS = getClass().getResourceAsStream("/collectables.png");
        InputStream carIS = getClass().getResourceAsStream("/cars.png");
        InputStream carOppsiteIS = getClass().getResourceAsStream("/cars-opposite.png");
        InputStream startIS = getClass().getResourceAsStream("/start.png");
        InputStream win1IS = getClass().getResourceAsStream("/1-win.png");
        InputStream win2IS = getClass().getResourceAsStream("/2-win.png");
        try {
            charImg = ImageIO.read(characterIS);
            backImg = ImageIO.read(backIS);
            startImg = ImageIO.read(startIS);
            collectableImg = ImageIO.read(collectablesIS);
            carImg = ImageIO.read(carIS);
            carImg2 = ImageIO.read(carOppsiteIS);
            win1Img = ImageIO.read(win1IS);
            win2Img = ImageIO.read(win2IS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void moveDelta2(int ani) {
        switch (ani) {
            case PLAYER_2_LEFT:
                xDelta2 -= 7;
                charAnimationCol2 = GetPlayer2AnimationCol(PLAYER_2_LEFT);
                charAnimationRow2 = GetPlayer2AnimationRow(PLAYER_2_LEFT);
                break;
            case PLAYER_2_RIGHT:
                xDelta2 += 7;
                charAnimationCol2 = GetPlayer2AnimationCol(PLAYER_2_RIGHT);
                charAnimationRow2 = GetPlayer2AnimationRow(PLAYER_2_RIGHT);
                break;
            case PLAYER_2_UP:
                yDelta2 -= 7;
                charAnimationCol2 = GetPlayer2AnimationCol(PLAYER_2_UP);
                charAnimationRow2 = GetPlayer2AnimationRow(PLAYER_2_UP);
                break;
            case PLAYER_2_DOWN:
                yDelta2 += 7;
                charAnimationCol2 = GetPlayer2AnimationCol(PLAYER_2_DOWN);
                charAnimationRow2 = GetPlayer2AnimationRow(PLAYER_2_DOWN);
                break;
            case PLAYER_2_IDLE:
                charAnimationCol2 = GetPlayer2AnimationCol(PLAYER_2_IDLE);
                charAnimationRow2 = GetPlayer2AnimationRow(PLAYER_2_IDLE);
                break;
        }
    }

    public void moveDelta(int ani) {
        switch (ani) {
            case PLAYER_1_LEFT:
                xDelta -= 7;
                charAnimationCol = GetPlayer1AnimationCol(PLAYER_1_LEFT);
                charAnimationRow = GetPlayer1AnimationRow(PLAYER_1_LEFT);
                break;
            case PLAYER_1_RIGHT:
                xDelta += 7;
                charAnimationCol = GetPlayer1AnimationCol(PLAYER_1_RIGHT);
                charAnimationRow = GetPlayer1AnimationRow(PLAYER_1_RIGHT);
                break;
            case PLAYER_1_UP:
                yDelta -= 7;
                charAnimationCol = GetPlayer1AnimationCol(PLAYER_1_UP);
                charAnimationRow = GetPlayer1AnimationRow(PLAYER_1_UP);
                break;
            case PLAYER_1_DOWN:
                yDelta += 7;
                charAnimationCol = GetPlayer1AnimationCol(PLAYER_1_DOWN);
                charAnimationRow = GetPlayer1AnimationRow(PLAYER_1_DOWN);
                break;
            case PLAYER_1_IDLE:
                charAnimationCol = GetPlayer1AnimationCol(PLAYER_1_IDLE);
                charAnimationRow = GetPlayer1AnimationRow(PLAYER_1_IDLE);
                break;
        }
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(RUNNING){
            g.drawImage(backImg, 0, 0, null);
            checkCollisionWithNutech();
            updateAnimations();
            drawPlayers(g);
            drawSamosa(g);
            drawDiamonds(g);
            drawCars(g);
            drawScores(g);
            checkWinner();
        } else {
            if(game_start == 0){
                setBackground(new Color(0xFD9900));
                g.drawImage(startImg, 0, 0, null);
            } else if (game_start == 1) {
                g.drawImage(win1Img, 0, 0, null);
            } else if (game_start == 2){
                g.drawImage(win2Img, 0, 0, null);
            }

        }
    }


    private void checkWinner(){
        if(player_1_samosas >= 5){
            RUNNING = false;
            game_start = 1;
        } else if (player_2_samosas >= 5) {
            RUNNING = false;
            game_start = 2;
        } else if (currentSamosas == 5 && !player1_has_samosa && !player2_has_samosa) {
            System.out.println("Draw");
        }
    }


    private void drawPlayers(Graphics g){
        if (player1_has_samosa){
            g.drawImage(player1Imgs[3][0].getScaledInstance(100, 100, 100), (int) xDelta, (int) yDelta, null);
        } else {
            g.drawImage(player1Imgs[charAnimationRow][aniIndex].getScaledInstance(100, 100, 100), (int) xDelta, (int) yDelta, null);
        }

        if(player2_has_samosa) {
            g.drawImage(player2Imgs[3][0].getScaledInstance(100, 100, 100), (int) xDelta2, (int) yDelta2, null);
        } else {
            g.drawImage(player2Imgs[charAnimationRow2][aniIndex2].getScaledInstance(100, 100, 100), (int) xDelta2, (int) yDelta2, null);
        }
    }

    private void drawScores(Graphics g){
        // player 1
        g.drawImage(player1Imgs[3][0].getScaledInstance(50, 50, 100), 120, 0, null);
        if(player_1_diamond){
            g.drawImage(collectableImgs[0][aniIndexCol].getScaledInstance(50,50,50), 160, 0, null);
        }
        for (int i = 0; i < player_1_samosas; i++){
            g.drawImage(collectableImgs[1][aniIndexCol].getScaledInstance(50,50,50), 160 + ((i+1) * 40), 0, null);
        }

        // player 2
        g.drawImage(player2Imgs[3][0].getScaledInstance(50, 50, 50), 120, 50, null);
        if(player_2_diamond){
            g.drawImage(collectableImgs[0][aniIndexCol].getScaledInstance(50,50,50), 160, 50, null);
        }
        for (int i = 0; i < player_2_samosas; i++){
            g.drawImage(collectableImgs[1][aniIndexCol].getScaledInstance(50,50,50), 160 + ((i+1) * 40), 50, null);
        }
    }


    private void drawSamosa(Graphics g) {
        int x = 1000;
        if(samosa_1) {
            g.drawImage(collectableImgs[1][aniIndexCol], x, 50, null);
            checkCollisionWithSamosa(x, 50, 1);
        }

        if(samosa_2){
            g.drawImage(collectableImgs[1][aniIndexCol], x, 200, null);
            checkCollisionWithSamosa(x, 200, 2);
        }

        if(samosa_3){
            g.drawImage(collectableImgs[1][aniIndexCol], x, 300, null);
            checkCollisionWithSamosa(x, 300, 3);
        }
        if(samosa_4){
            g.drawImage(collectableImgs[1][aniIndexCol], x, 400, null);
            checkCollisionWithSamosa(x, 400, 4);
        }
        if(samosa_5){
            g.drawImage(collectableImgs[1][aniIndexCol], x, 500, null);
            checkCollisionWithSamosa(x, 500, 5);
        }
    }

    private void addPlayer1Samosa(){
            if(player_1_diamond){
                this.player_1_samosas += 2;
            } else {
                this.player_1_samosas += 1;
            }
    }

    private void removePlayer1Samosa(){
        if(player_1_samosas > 0){
            this.player_1_samosas -= 1;
        }
    }

    private void addPlayer2Samosa(){
            if(player_2_diamond){
                this.player_2_samosas += 2;
            }else {
                this.player_2_samosas += 1;
            }

    }

    private void removePlayer2Samosa(){
        if(player_2_samosas > 0){
            this.player_1_samosas -= 1;
        }
    }


    private void drawDiamonds(Graphics g) {
        int x = 0, y = 0;
        switch (maxDiamonds) {
            case 0:
                x = 600;
                y = 178;
                g.drawImage(collectableImgs[0][aniIndexCol], x, y, null);
                checkCollisionWithDiamonds(x, y);
                maxDiamonds= 5;
                break;
            case 1:
                x = 630;
                y = 530;
                g.drawImage(collectableImgs[0][aniIndexCol], x, y, null);
                checkCollisionWithDiamonds(x, y);

                break;
            case 2:
                x = 244;
                y = 304;
                g.drawImage(collectableImgs[0][aniIndexCol], x, y, null);
                checkCollisionWithDiamonds(x, y);
                break;
            case 3:
                x = 172;
                y = 307;
                g.drawImage(collectableImgs[0][aniIndexCol], x, y, null);
                checkCollisionWithDiamonds(x, y);
                break;
            case 4:
                x = 200;
                y = 531;
                g.drawImage(collectableImgs[0][aniIndexCol], x, y, null);
                checkCollisionWithDiamonds(x, y);
                break;
            case 5:
                x = 800;
                y = 120;
                g.drawImage(collectableImgs[0][aniIndexCol], x, y, null);
                checkCollisionWithDiamonds(x, y);
                break;
        }
    }

    private void drawCars(Graphics g) {
        for (int i = 0; i < 9; i++) {
            if (i > 0 && i <= 3) {
                g.drawImage(carImgs[i][0], carXDeltaLine1, carYDelta + (300 * i), null);
                checkCollisionWithCars(carXDeltaLine1, carYDelta + (300 * i));
                g.drawImage(carImgs2[i][0], carXDeltaLine3, carYDeltaOpposite - (100 * i), null);
                checkCollisionWithCars(carXDeltaLine3, carYDeltaOpposite - (100 * i));
                g.drawImage(carImgs2[i][0], carXDeltaLine4, carYDeltaOpposite2 - (500 * i), null);
                checkCollisionWithCars(carXDeltaLine4, carYDeltaOpposite2 - (500 * i));
            } else if (i > 3 && i <= 6) {
                g.drawImage(carImgs[i][0], carXDeltaLine2, carYDelta2 + (600 * i), null);
                checkCollisionWithCars(carXDeltaLine2, carYDelta2 + (600 * i));
                g.drawImage(carImgs2[i][0], carXDeltaLine3, carYDeltaOpposite - (150 * i), null);
                checkCollisionWithCars(carXDeltaLine3, carYDeltaOpposite - (150 * i));
            } else {
                g.drawImage(carImgs[i][0], carXDeltaLine2, carYDelta2 + (200 * i), null);
                checkCollisionWithCars(carXDeltaLine2, carYDelta2 + (200 * i));
                g.drawImage(carImgs[i][0], carXDeltaLine1, carYDelta + (350 * i), null);
                checkCollisionWithCars(carXDeltaLine1, carYDelta + (350 * i));
                g.drawImage(carImgs2[i][0], carXDeltaLine4, carYDeltaOpposite2 - (600 * i), null);
                checkCollisionWithCars(carXDeltaLine4, carYDeltaOpposite2 - (600 * i));
            }
        }
    }

    private boolean checkCollisionOfPlayer1(int x, int y, int range) {
        //System.out.println(xDelta + ", " + yDelta);
        int xMinus = (int) x - range;
        int xPlus = (int) x + range;
        int yMinus = (int) y - range;
        int yPlus = (int) y + range;

        if ((xDelta <= xPlus && xDelta >= xMinus) && (yDelta <= yPlus && yDelta >= yMinus)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkCollisionOfPlayer2(int x, int y, int range) {
        //System.out.println(xDelta + ", " + yDelta);
        int xMinus = (int) x - range;
        int xPlus = (int) x + range;
        int yMinus = (int) y - range;
        int yPlus = (int) y + range;

        if ((xDelta2 <= xPlus && xDelta2 >= xMinus) && (yDelta2 <= yPlus && yDelta2 >= yMinus)) {
            return true;
        } else {
            return false;
        }
    }

    public void checkCollisionWithNutech(){
        int range = 50;
        if(checkCollisionOfPlayer1(-17, 126, range) || checkCollisionOfPlayer1(39, 126, range) || checkCollisionOfPlayer1(39, -14, range) || checkCollisionOfPlayer1(-17, -7, range)){
            if(player1_has_samosa){
                addPlayer1Samosa();
                player1_has_samosa = false;
                player_1_diamond = false;

            }
        }
        if(checkCollisionOfPlayer2(-17, 126, range) || checkCollisionOfPlayer2(39, 126, range) || checkCollisionOfPlayer2(39, -14, range) || checkCollisionOfPlayer2(-17, -7, range)){
            if(player2_has_samosa){
                addPlayer2Samosa();
                player2_has_samosa  = false;
                player_2_diamond = false;
            }
        }
    }

    private void checkCollisionWithCars(int x, int y) {
        if (checkCollisionOfPlayer1(x, y, 50)) {
            removePlayer1Samosa();
            player1_has_samosa = false;
            respawAfterCollision(1);
        }

        if (checkCollisionOfPlayer2(x, y, 50)) {
            removePlayer2Samosa();
            player2_has_samosa = false;
            respawAfterCollision(2);
        }
    }

    private void respawAfterCollision(int player){
        if(player == 1){
            xDelta = XSTARTINGPOS;
            yDelta = YSTARTINGPOS;
        }
        if(player == 2){
            xDelta2 = XSTARTINGPOS2;
            yDelta2 = YSTARTINGPOS2;
        }
    }

    private void checkCollisionWithSamosa(int x, int y, int samosa_number) {

        if (checkCollisionOfPlayer1(x, y, 50) && !player1_has_samosa) {
            player1_has_samosa = true;
            hideCollidedSamosa(samosa_number);
        }

        if (checkCollisionOfPlayer2(x, y, 50) && !player2_has_samosa) {
            player2_has_samosa = true;
            hideCollidedSamosa(samosa_number);
        }


    }


    private void hideCollidedSamosa(int samosa_number){
        currentSamosas = 0;
        switch (samosa_number){
            case 1:
                samosa_1 = false;
                currentSamosas++;
                break;
            case 2:
                samosa_2 = false;
                currentSamosas++;
                break;
            case 3:
                samosa_3 = false;
                currentSamosas++;
                break;
            case 4:
                samosa_4 = false;
                currentSamosas++;
                break;
            case 5:
                samosa_5 = false;
                currentSamosas++;
                break;
        }
    }

    private void checkCollisionWithDiamonds(int x, int y) {
        if (checkCollisionOfPlayer1(x, y, 50)) {
            player_1_diamond = true;
            maxDiamonds--;
        }

        if (checkCollisionOfPlayer2(x, y, 50)) {
            player_2_diamond = true;
            maxDiamonds--;
        }
    }

    public void updateAnimations() {
        updatePlayer1Animation();
        updatePlayer2Animation();
        updateCollectableAnimation();
        updateMoveCar();
        //updateRoadLines();
    }

    private void updatePlayer2Animation() {
        aniTick2++;
        if (aniTick2 >= aniSpeed2) {
            aniTick2 = 0;
            aniIndex2++;
            if (aniIndex2 >= charAnimationCol2) {
                aniIndex2 = 0;
            }
        }
    }

    private void updatePlayer1Animation() {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= charAnimationCol) {
                aniIndex = 0;
            }
        }
    }

    private void updateMoveCar() {
        aniTickCar++;
        if (aniTickCar >= aniSpeedCar) {
            aniTickCar = 0;
            carYDelta -= 15;
            carYDelta2 -= 15;
            carYDeltaOpposite += 15;
            carYDeltaOpposite2 += 15;
        }

        if ((carYDelta + (600 * 9)) <= -100) {
            carYDelta = CAR_Y_DELTA;
        }

        if ((carYDelta2 + (600 * 9)) <= -100) {
            carYDelta2 = CAR_Y_DELTA;
        }


        if ((carYDeltaOpposite - (600 * 9) >= CAR_Y_DELTA)) {
            carYDeltaOpposite = CAR_Y_DELTAOPPOSITE;
        }

        if ((carYDeltaOpposite2 - (600 * 9) >= CAR_Y_DELTA)) {
            carYDeltaOpposite2 = CAR_Y_DELTAOPPOSITE;
        }
    }

    private void updateCollectableAnimation() {
        aniTickCol++;
        if (aniTickCol >= aniSpeedCol) {
            aniTickCol = 0;
            aniIndexCol++;
            if (aniIndexCol >= 9) {
                aniIndexCol = 0;
            }
        }
    }

}
