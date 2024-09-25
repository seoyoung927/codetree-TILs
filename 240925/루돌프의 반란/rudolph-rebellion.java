import java.util.*;
import java.io.*;

public class Main {
    static int N,M,P,C,D;
    static int[][] map;
    static Point rudolph;
    static Point[] santa;
    static int[] drr = {-1,-1,0,1,1,1,0,-1}; //상,우상,우,우하,하,좌하,좌,좌상
    static int[] drc = {0,1,1,1,0,-1,-1,-1};
    static int[] dsr = {0,1,0,-1}; //좌,하,우,상
    static int[] dsc = {-1,0,1,0};
    static int outCnt;

    static class Point{
        int r;
        int c;
        int score;
        boolean isAlive;
        int turn;

        public Point() {}

        public Point(int r, int c){
            this.r=r;
            this.c=c;
        }

        public Point(int r, int c, int score, boolean isAlive){
            this.r=r;
            this.c=c;
            this.score=score;
            this.isAlive=isAlive;
        }

        @Override
        public String toString(){
            return "r="+r+",c="+c+",socre="+score+",isAlive="+isAlive;
        }
    }

    public static int getDistance(Point p1, Point p2){
        return (int) Math.pow(p1.r-p2.r,2) + (int) Math.pow(p1.c-p2.c,2);
    }

    public static int getSantaIdx(){
        int ret = 0;
        int minVal = Integer.MAX_VALUE;

        for(int i=1; i<P+1; i++){
            if(santa[i].isAlive==false) continue;

            int val = getDistance(rudolph, santa[i]);

            if(val<minVal){
                minVal = val;
                ret = i;
            }else if(val==minVal){
                if(santa[i].r>santa[ret].r || (santa[i].r==santa[ret].r && santa[i].c>santa[ret].c)){
                    minVal = val;
                    ret = i;
                }
            }
        }

        return ret;
    }

    public static int getRudolphDirection(int santaIdx){
        int ret = 0;
        int minVal = getDistance(rudolph, santa[santaIdx]);

        for(int i=0; i<8; i++){
            int nrr = rudolph.r + drr[i];
            int nrc = rudolph.c + drc[i];

            // 만약 판 밖으로 나갔다면
            if(nrr<=0 || nrr>N || nrc<=0 || nrc>N) continue;

            int val = getDistance(new Point(nrr, nrc), santa[santaIdx]);

            if(val<minVal){
                minVal = val;
                ret = i;
            }
        }

        return ret;
    }

    public static int getSantaDirection(int santaIdx){
        int ret = -1;
        int minVal = getDistance(rudolph, santa[santaIdx]);

        for(int i=0; i<4; i++){
            int nsr = santa[santaIdx].r + dsr[i];
            int nsc = santa[santaIdx].c + dsc[i];
            if(nsr<=0 || nsr>N || nsc<=0 || nsc>N) continue;
            if(map[nsr][nsc]>0) continue;
            int val = getDistance(rudolph, new Point(nsr, nsc));
            if(val<=minVal){
                minVal=val;
                ret = i;
            }
        }

        return ret;
    }

    public static void moveSanta(int santaIdx, int dr, int dc, int dd){
        int csr = santa[santaIdx].r;
        int csc = santa[santaIdx].c;

        int nsr = csr+dr*dd;
        int nsc = csc+dc*dd;

        if(nsr<=0 || nsr>N || nsc<=0 || nsc>N) {
            map[csr][csc] = -1;
            santa[santaIdx].isAlive = false;
            outCnt++;
            return;
        }

        if(map[nsr][nsc]!=-1){
            moveSanta(map[nsr][nsc], dr, dc, 1);
        }

        map[csr][csc] = -1;
        map[nsr][nsc] = santaIdx;
        santa[santaIdx].r = nsr;
        santa[santaIdx].c = nsc;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        P = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        D = Integer.parseInt(st.nextToken());

        map = new int[N+1][N+1];
        for(int r=0; r<N+1; r++) Arrays.fill(map[r], -1);

        st = new StringTokenizer(br.readLine());
        int Rr = Integer.parseInt(st.nextToken());
        int Rc = Integer.parseInt(st.nextToken());
        rudolph = new Point(Rr, Rc);
        map[Rr][Rc] = 0;

        santa = new Point[P+1];
        santa[0] = new Point();
        for(int i=1; i<P+1; i++){
            st = new StringTokenizer(br.readLine());
            int idx = Integer.parseInt(st.nextToken());
            int Sr = Integer.parseInt(st.nextToken());
            int Sc = Integer.parseInt(st.nextToken());
            map[Sr][Sc] = idx;
            santa[idx] = new Point(Sr, Sc, 0, true);
        }
        outCnt = 0;


        // 게임 시작
        for(int turn = 1; turn<=M; turn++){
            if(outCnt==P) break;
            // 루돌프
            int nearestSanta = getSantaIdx();
            int rudolphDirection = getRudolphDirection(nearestSanta);
            // 루돌프 이동
            int nrr = rudolph.r+drr[rudolphDirection];
            int nrc = rudolph.c+drc[rudolphDirection];
            if(map[nrr][nrc]!=-1){
                int moveSantaIdx = map[nrr][nrc];
                santa[moveSantaIdx].score+=C;
                santa[moveSantaIdx].turn=turn+1;
                moveSanta(moveSantaIdx, drr[rudolphDirection], drc[rudolphDirection], C);
            }
            map[rudolph.r][rudolph.c] = -1;
            map[nrr][nrc] = 0;
            rudolph.r = nrr;
            rudolph.c = nrc;

            // 산타
            for(int santaIdx=1; santaIdx<=P; santaIdx++){
                if(outCnt==P) break;
                if(santa[santaIdx].isAlive==false) continue;
                if(santa[santaIdx].turn>=turn) continue;

                int santaDirection = getSantaDirection(santaIdx);
                if(santaDirection==-1) continue;

                // 산타 이동
                int nsr = santa[santaIdx].r+dsr[santaDirection];
                int nsc = santa[santaIdx].c+dsc[santaDirection];
                if(map[nsr][nsc]==0){ // 루돌프와 충돌
                    santa[santaIdx].score+=D;
                    santa[santaIdx].turn=turn+1;
                    nsr = rudolph.r - dsr[santaDirection]*D;
                    nsc = rudolph.c - dsc[santaDirection]*D;
                    if(nsr<=0 || nsr>N || nsc<=0 || nsc>N) {
                        map[santa[santaIdx].r][santa[santaIdx].c] = -1;
                        santa[santaIdx].isAlive = false;
                        outCnt++;
                        continue;
                    }else if(map[nsr][nsc]>0 && map[nsr][nsc]!=santaIdx){
                        // 상호작용 발생
                        moveSanta(map[nsr][nsc], -1* dsr[santaDirection], -1 *dsc[santaDirection] , 1);
                    }
                    map[santa[santaIdx].r][santa[santaIdx].c] = -1;
                    map[nsr][nsc] = santaIdx;
                    santa[santaIdx].r = nsr;
                    santa[santaIdx].c = nsc;
                }else{
                    map[santa[santaIdx].r][santa[santaIdx].c] = -1;
                    map[nsr][nsc] = santaIdx;
                    santa[santaIdx].r = nsr;
                    santa[santaIdx].c = nsc;
                }
            }

            for(int santaIdx=1; santaIdx<=P; santaIdx++){
                if(santa[santaIdx].isAlive) santa[santaIdx].score += 1;
            }
        }

        // Arrays.sort(santa, (Point s1, Point s2) -> Integer.compare(s1.score, s2.score));

        for(int santaIdx=1; santaIdx<=P; santaIdx++){
            System.out.print(santa[santaIdx].score+" ");
        }
    }
}