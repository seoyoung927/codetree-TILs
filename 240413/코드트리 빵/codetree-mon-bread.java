import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Arrays;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;

public class Main {
    static int n,m; //n: 격자의 크기, m: 사람의 수
    static int[][] board;
    static Point[] stores;
    static ArrayList<Point> basecamps;
    static Point[] players;
    static boolean[] isArrive;
    static int[] dr = {-1,0,0,1}; //상,좌,우,하
    static int[] dc = {0,-1,1,0}; //상,좌,우,하

    static class Point{
        int r,c;
        public Point(int r, int c){
            this.r=r;
            this.c=c;
        }
        @Override
        public String toString(){
            return "(r="+r+", c="+c+")";
        }
    }

    static class Node{
        int r,c;
        int dist;
        boolean[][] selected;

        public Node(int r, int c, int dist, boolean[][] selected){
            this.r=r;
            this.c=c;
            this.dist=dist;
            this.selected=selected;
        }
    }

    public static Point getNextPoint(Point start, Point target){
        Point next = new Point(-1,-1);
        ArrayList<boolean[][]> paths = new ArrayList<>();
        int minDist = Integer.MAX_VALUE;

        Queue<Node> q = new ArrayDeque<>();
        boolean[][] selected = new boolean[n+1][n+1];
        selected[start.r][start.c] = true;
        q.add(new Node(start.r,start.c,0,selected));
        while(!q.isEmpty()){
            Node cur = q.poll();
            int cr = cur.r;
            int cc = cur.c;
            int cDist = cur.dist;
            boolean[][] cSelected = cur.selected;

            if(cr==target.r && cc==target.c){
                // for(int r=0; r<n+1; r++){
                //     System.out.println(Arrays.toString(cSelected[r]));
                // }
                if(cDist<minDist){
                    minDist=cDist;
                    paths.clear();
                    paths.add(cSelected);
                }else if(cDist==minDist){
                    paths.add(cSelected);
                }
                break;
            }

            for(int i=0; i<4; i++){
                int nr = cr+dr[i];
                int nc = cc+dc[i];
                
                if(nr<=0 || nr>n || nc<=0 || nc>n) continue;
                if(board[nr][nc]==-1) continue;
                if(cSelected[nr][nc]) continue;

                boolean[][] newSelected=new boolean[n+1][n+1];
                for(int r=0; r<n+1; r++){
                    for(int c=0; c<n+1; c++){
                        newSelected[r][c]=cSelected[r][c];
                    }
                }
                newSelected[nr][nc]=true;
                q.add(new Node(nr,nc,cDist+1,newSelected));
            }
        }

        ArrayList<Integer> candidates = new ArrayList<>();
        for(boolean[][] path : paths){
            for(int i=0; i<4; i++){
                int nr = start.r+dr[i];
                int nc = start.c+dc[i];
                if(nr<=0 || nr>n || nc<=0 || nc>n) continue;
                if(path[nr][nc]){
                    candidates.add(i);
                    break;
                }
            }
        }
        Collections.sort(candidates);
        int d = candidates.get(0);
        int nr = start.r+dr[d];
        int nc = start.c+dc[d];
        next = new Point(nr,nc);

        return next;
    }

    static class NodeDist{
        int r,c,dist;
        public NodeDist(int r, int c, int dist){
            this.r=r;
            this.c=c;
            this.dist=dist;
        }
    }
    
    public static Point getNearestCamp(Point store){
        ArrayList<Point> candidates = new ArrayList<>();
        int minDist = Integer.MAX_VALUE;

        for(Point bc : basecamps){
            //이미 선택된 basecamp라면
            if(board[bc.r][bc.c]==-1) continue;
            Queue<NodeDist> q = new ArrayDeque<>();
            boolean[][] visited = new boolean[n+1][n+1];

            q.add(new NodeDist(bc.r,bc.c,0));
            visited[bc.r][bc.c]=true;
            while(!q.isEmpty()){
                NodeDist cur = q.poll();
                int cr = cur.r;
                int cc = cur.c;
                int cdist = cur.dist;
                
                if(cr==store.r && cc==store.c){
                    if(cdist<minDist){
                        minDist=cdist;
                        candidates.clear();
                        candidates.add(bc);
                    }else if(cdist==minDist){
                        candidates.add(bc);
                    }
                    break;
                }
                
                for(int i=0; i<4; i++){
                    int nr = cr + dr[i];
                    int nc = cc + dc[i];
                    
                    if(nr<=0 || nr>n || nc<=0 || nc>n) continue;
                    if(visited[nr][nc]) continue;
                    if(board[nr][nc]==-1) continue;

                    q.add(new NodeDist(nr,nc,cdist+1));
                    visited[nr][nc]=true;
                }
            }
        }

        //System.out.println(candidates);
        Collections.sort(candidates, (a,b)->{
            if(a.r==b.r) return a.c-b.c;
            return a.r-b.r;
        });

        return candidates.get(0);
    }


    public static void main(String[] args) throws Exception{
        //0. 입력
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());

        board = new int[n+1][n+1];
        basecamps = new ArrayList<>();
        for(int r=1; r<n+1; r++){
            st = new StringTokenizer(br.readLine());
            for(int c=1; c<n+1; c++){
                board[r][c] = Integer.parseInt(st.nextToken());
                if(board[r][c]==1) basecamps.add(new Point(r,c));
            }
        }

        stores = new Point[m+1];
        isArrive = new boolean[m+1];
        for(int i=1; i<m+1; i++){
            st = new StringTokenizer(br.readLine());
            int r = Integer.parseInt(st.nextToken());
            int c = Integer.parseInt(st.nextToken());
            stores[i] = new Point(r,c);
        }

        //2. t초동안 로직 수행
        players = new Point[m+1];
        int t=1;
        while(true){
            //1. 사람들이 이동
            ArrayList<Point> deletePoints = new ArrayList<>();
            for(int i=1; i<Math.min(t, m+1); i++){
                if(isArrive[i]) continue; //i번째 player가 이미 편의점에 도착했다면 다음으로
                
                Point next = getNextPoint(players[i], stores[i]);
                //System.out.println(i+"번째 player: "+next);
                players[i] = next;
                if(next.r==stores[i].r && next.c==stores[i].c){
                    deletePoints.add(next);
                    isArrive[i]=true;
                }
            }

            for(Point deletePoint : deletePoints){
                board[deletePoint.r][deletePoint.c]=-1;
            }

            if(t<=m){
                Point bc = getNearestCamp(stores[t]);
                //이때부터 다른 사람들은 해당 베이스캠프가 있는 칸을 지나갈 수 없게 됨
                //t번 사람이 편의점을 향해 움직이기 시작했더라도 해당 베이스캠프는 앞으로 절대 지나갈 수 없음
                //마찬가지로 격자에 있는 사람들이 모두 이동한 뒤에 해당 칸을 지나갈 수 없어짐
                board[bc.r][bc.c]=-1;
                players[t]=new Point(bc.r,bc.c);
            }       

            //모든 사람들이 이동하였는지 확인
            boolean flag = true;
            for(int i=1; i<m+1; i++){
                if(!isArrive[i]){
                    flag = false;
                    break;
                }
            }
            if(flag) break;

            // System.out.println(t+": ");
            // for(int r=0; r<n+1; r++){
            //     System.out.println(Arrays.toString(board[r]));
            // }

            t++;
        }

        //END: 출력
        System.out.println(t);
    }
}