import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ArrayDeque;
import java.util.Queue;

public class Main {
    static int N,M,K; //N: 세로, M: 가로, K: 턴수
    static Turret[][] board;
    static ArrayList<Turret> turrets;
    static int[] dr = {0,1,0,-1}; //우하좌상
    static int[] dc = {1,0,-1,0}; //우하좌상
    //포탑 공격에 사용되는 방향
    static int[] dr2 = {0,-1,-1,0,1,1,1,0,-1}; //공격대상,상,우상,우,우하,하,좌하,좌,좌상
    static int[] dc2 = {0,0,1,1,1,0,-1,-1,-1}; //공격대상,상,우상,우,우하,하,좌하,좌,좌상

    static class Point{
        int r,c;
        boolean[][] selected;
        public Point(int r, int c,boolean[][] selected){
            this.r=r;
            this.c=c;
            this.selected=selected;
        }
    }

    static class Turret{
        int r, c;
        int power;
        int recentAttack;
        public Turret(int r, int c, int power, int recentAttack){
            this.r=r;
            this.c=c;
            this.power=power;
            this.recentAttack=recentAttack;
        }
        @Override
        public String toString(){
            //return "(r: "+r+", c="+c+", power="+power+", recentAttack="+recentAttack+")";
            return "("+power+")";
        }
    }

    public static void main(String[] args) throws Exception{
        //1. 입력
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
    
        board = new Turret[N+1][M+1]; //좌상단 좌표 (1,1)
        turrets = new ArrayList<>();
        int idx = 0;
        for(int r=1; r<N+1; r++){
            st = new StringTokenizer(br.readLine());
            for(int c=1; c<M+1; c++){
                int p = Integer.parseInt(st.nextToken());
                board[r][c] = new Turret(r,c,p,0);
                if(p>0) turrets.add(board[r][c]);
            }
        }

        for(int turn=1; turn<K+1; turn++){
            //2-1. 공격자의 선정(가장 약한 포탑)
            Collections.sort(turrets, (a,b)->{
                if(a.power!=b.power) return a.power-b.power; //공격력이 가장 낮은 포탑
                if(a.recentAttack!=b.recentAttack) return b.recentAttack-a.recentAttack; //가장 최근에 공격한 포탑
                if((a.r+a.c)!=(b.r+b.c)) return (b.r+b.c)-(a.r+a.c); //행과 열의 합이 가장 큰 포탑
                return (b.r-a.r);
            });
            Turret attacker = turrets.get(0);
            //System.out.println(attacker);

            //2-2. 공격자의 공격
            //공격대상 선정(가장 강한 포탑)
            Collections.sort(turrets, (a,b)->{
                if(a.power!=b.power) return b.power-a.power; //공격력이 가장 높은 포탑
                if(a.recentAttack!=b.recentAttack) return a.recentAttack-b.recentAttack; //가장 오래된 공격한 포탑
                if((a.r+a.c)!=(b.r+b.c)) return (a.r+a.c)-(b.r+b.c); //행과 열의 합이 가장 작은 포탑
                return (a.r-b.r);

            });
            Turret attacked = turrets.get(0);
            //System.out.println(attacked);

            //2-3. 레이저 공격 bfs
            //2-3-1. 레이저 공격
            attacker.power += (N+M);
            attacker.recentAttack = turn;
            boolean isLaserAttackPossible = false;
            boolean[][] selected = new boolean[N+1][M+1];

            Queue<Point> q = new ArrayDeque<>();
            boolean[][] visited = new boolean[N+1][M+1];
            q.add(new Point(attacker.r,attacker.c,selected));
            visited[attacker.r][attacker.c]=true;
            while(!q.isEmpty()){
                Point cur = q.poll();
                int cr = cur.r;
                int cc = cur.c;
                boolean[][] cSelected = cur.selected;

                if(cr==attacked.r && cc==attacked.c) {
                    //2-3-2. 레이저 공격이 가능하다면 공격
                    //System.out.println("레이저 공격 가능");
                    for(int r=0; r<N+1; r++){
                        for(int c=0; c<M+1; c++){
                            selected[r][c] = cSelected[r][c];
                            if(cSelected[r][c]){
                                if(r==attacked.r && c==attacked.c){
                                    board[r][c].power-=attacker.power;
                                }else{
                                    board[r][c].power-=attacker.power/2;
                                }
                                if(board[r][c].power<=0) board[r][c].power=0;
                                //공격력이 0이하가 되었다면
                                if(board[r][c].power<=0) turrets.remove(board[r][c]);
                            }
                        }
                    }
                    isLaserAttackPossible = true;
                    break;
                }
                
                for(int i=0; i<4; i++){
                    int nr = cr + dr[i];
                    int nc = cc + dc[i];
                    if(nr==0) nr=N;
                    if(nr==N+1) nr=1;
                    if(nc==0) nc=M;
                    if(nc==M+1) nc=1; //가장자리에서 막힌 방향으로 진행하고자 한다면, 반대편으로 나온다.
                    if(board[nr][nc].power==0) continue; //부서진 포탑이 있는 위치는 지날 수 없다.
                    if(visited[nr][nc]) continue;
                    boolean[][] newSelected = new boolean[N+1][M+1];
                    for(int r=0; r<N+1; r++){
                        for(int c=0; c<M+1; c++){
                            newSelected[r][c] = cSelected[r][c];
                        }
                    }
                    newSelected[nr][nc] = true;
                    q.offer(new Point(nr,nc,newSelected));
                    visited[nr][nc]=true;
                }
            }
            
            // for(int r=0; r<N+1; r++){
            //     System.out.println(Arrays.toString(board[r]));
            // }

            //2-3-3. 레이저 공격이 불가능하다면 포탑 공격 
            if(!isLaserAttackPossible){
                selected = new boolean[N+1][M+1];
                int cr= attacked.r;
                int cc = attacked.c;
                for(int i=0; i<9; i++){
                    int nr = cr+dr2[i];
                    int nc = cc+dc2[i];
                    //if(nr<=0 || nr>N || nc<=0 || nc>M) continue;
                    if(nr==0) nr=N;
                    if(nr==N+1) nr=1;
                    if(nc==0) nc=M;
                    if(nc==M+1) nc=1; //가장자리에서 막힌 방향으로 진행하고자 한다면, 반대편으로 나온다.
                    if(nr==attacker.r && nc==attacker.c) continue; 
                    if(nr==attacked.r && nc==attacked.c) board[nr][nc].power-=attacker.power;
                    else board[nr][nc].power-=attacker.power/2;
                    if(board[nr][nc].power<=0) board[nr][nc].power=0;
                    selected[nr][nc]=true;
                    if(board[nr][nc].power<0) turrets.remove(board[nr][nc]);
                }
            }

            //2-4. 포탑 재정비
            selected[attacker.r][attacker.c]=true;
            for(int r=1; r<N+1; r++){
                for(int c=1; c<M+1; c++){
                    if(board[r][c].power>0 && !selected[r][c]) board[r][c].power+=1;
                    if(board[r][c].power<=0) board[r][c].power=0;
                }
            }
            // for(int r=0; r<N+1; r++){
            //     System.out.println(Arrays.toString(board[r]));
            // }
            // System.out.println();
        }


        //3. 출력
        Collections.sort(turrets, (a,b)->{
            if(a.power!=b.power) return b.power-a.power; //공격력이 가장 높은 포탑
            if(a.recentAttack!=b.recentAttack) return a.recentAttack-b.recentAttack; //가장 오래된 공격한 포탑
            if((a.r+a.c)!=(b.r+b.c)) return (a.r+a.c)-(b.r+b.c); //행과 열의 합이 가장 작은 포탑
            return (a.r-b.r);

        });
        //System.out.println(turrets);
        Turret result = turrets.get(0);
        System.out.println(result.power);

    }
}