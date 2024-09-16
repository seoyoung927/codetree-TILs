import java.util.*;
import java.io.*;

public class Main {
    static int N,M,P,C,D;
    static int[][] map; // 게임판, 좌상단(1,1)
    static Point[] point;
    static Santa[] santa;
    static int outCnt; // 탈락한 산타의 수
    static int[] drr = {-1,-1,0,1,1,1,0,-1}; //상,우상,우,우하,하,좌하,좌,좌상
    static int[] drc = {0,1,1,1,0,-1,-1,-1}; //상,우상,우,우하,하,좌하,좌,좌상
    static int[] dsr = {0,1,0,-1}; // 좌,하,우,상
    static int[] dsc = {-1,0,1,0};
    
    static class Point{
        int r;
        int c;

        Point() {}

        Point(int r, int c){
            this.r=r;
            this.c=c;
        }
        
        @Override
        public String toString() {
        	return "r="+r+",c="+c;
        }
    }
    
    static class Santa{
    	int point;
    	boolean isOut;
    	int isSleep;
    	
    	Santa() {}
    	
    	Santa(int point, boolean isOut, int isSleep){
    		this.point = point;
    		this.isOut = isOut;
    		this.isSleep = isSleep;
    	}
    	
    	@Override
    	public String toString() {
    		return "point="+point+",isOut="+isOut+",isSleep="+isSleep;
    	}
    }
    
    public static int getDist(Point p1, Point p2) {
    	return (int) (Math.pow(p1.r-p2.r, 2)+Math.pow(p1.c-p2.c, 2));
    }
    

    /**
     * santaIdx1이 dr & dc방향으로 움직여서 santaIdx2와 충돌
     * 산타는 충돌 후 착지하게 되는 칸에 다른 산타가 있다면 그 산타는 1칸 해당 방향으로 밀려나게 된다.
     * @param santaIdx1
     * @param santaIdx2
     * @param dr
     * @param dc
     */
    public static void collision(int santaIdx, int dr, int dc) {
    	int curR = point[santaIdx].r; //충돌한 산타의 새로운 위치=충돌당한 산타의 위치
    	int curC = point[santaIdx].c;
    	int moveIdx = map[curR][curC];
    	int nextR = curR+dr;
    	int nextC = curC+dc;
    	if(nextR<=0 || nextR>N || nextC<=0 || nextC>N) {
        	// santaIdx2 게임에서 탈락
    		santa[moveIdx].isOut = true;
    		outCnt++;
    		map[point[moveIdx].r][point[moveIdx].c]=-1;
    		return;
    	}
    	
    	if(map[nextR][nextC]>0) {
    		// moveIdx의 이동으로 또 충돌 발생
    		collision(map[nextR][nextC], dr, dc);
    	}
    	
		map[curR][curC] = -1;
		map[nextR][nextC] = moveIdx;
		point[moveIdx] = new Point(nextR, nextC);
		
    }
    
    public static void main(String[] args) throws IOException{
        // 1. 입력 및 초기화
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        P = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        D = Integer.parseInt(st.nextToken());
        map = new int[N+1][N+1];
        for(int r=0; r<N+1; r++) Arrays.fill(map[r], -1);
        point = new Point[N+1];
        for(int i=0; i<P+1; i++) point[i] = new Point();
        santa = new Santa[P+1];
        for(int i=0; i<P+1; i++) santa[i] = new Santa();
        // 1-1. 루돌프의 초기위치 Rr, Rc
        st = new StringTokenizer(br.readLine());
        point[0].r = Integer.parseInt(st.nextToken());
        point[0].c = Integer.parseInt(st.nextToken());
        // 1-1-2. map에 루돌프의 위치 표시
        map[point[0].r][point[0].c] = 0;
        // 1-2. 산타들의 초기화위 Sr, Sc
        for(int i=1; i<P+1; i++) {
            st = new StringTokenizer(br.readLine());
            int idx = Integer.parseInt(st.nextToken());
            point[idx].r = Integer.parseInt(st.nextToken());
            point[idx].c = Integer.parseInt(st.nextToken());        	
            // 1-2-2. map에 산타의 위치 표시
            map[point[idx].r][point[idx].c] = idx;
        }

        
        // 2. M개의 턴
        for(int turn=1; turn<=M; turn++) {
    		if(outCnt==P) break;
        	
        	// 만약 P명의 산타가 모두 게임에서 탈락한다면 그 즉시 게임이 종료
        	for(int i=0; i<P+1; i++) { // 루돌프, 1~P의 산타들이 움직임
        		// 만약 P명의 산타가 모두 게임에서 탈락한다면 그 즉시 게임이 종료
        		if(outCnt==P) break;
        		
        		if(i==0) { 
        			// 2-1. 루돌프는 가장 가까운 산타를 향해서 1칸 돌진
            		// 2-2-1. 가장 가까운 산타를 찾음
        			int targetP = 1;
            		int minVal = Integer.MAX_VALUE;
            		for(int p=1; p<=P; p++) {
            			// 탈락한 santa면 pass
            			// 루돌프는 기절한 산타는 돌진대상으로 선택할 수 있다.
            			if(santa[p].isOut) continue;
            			
            			int dist = getDist(point[0], point[p]);
            			if(dist<minVal) {
            				minVal = dist;
             				targetP = p;
            			}
            			
            			else if(dist==minVal) {
            				// r좌표를 비교
            				if(point[p].r>point[targetP].r) {
            					targetP = p;
            				}
            				
            				// r좌표가 같다면 c좌표를 비교
            				else if(point[p].r==point[targetP].r && point[p].c>point[targetP].c) {
        						targetP = p;
            				}
            			}
            		}

            		// 2-2-2. 가장 가까운 산타와 가장 가까워지는 방향
            		int targetD = 0;
            		int minVal2 = getDist(point[0], point[targetP]);
            		for(int d=0; d<8; d++) {
            			int nextR = point[0].r+drr[d];
            			int nextC = point[0].c+drc[d];
            			if(nextR<=0 || nextR>N || nextC<=0 || nextC>N) continue;
            			int dist = getDist(new Point(nextR, nextC), point[targetP]);
            			if(dist<minVal2) {
            				minVal2 = dist;
            				targetD = d;
            			}
            		}
            		
            		int nextRR = point[0].r+drr[targetD];
            		int nextRC = point[0].c+drc[targetD];
            		
            		if(map[nextRR][nextRC]>0) {
            			// 루돌프가 움직여서 충돌이 발생한 경우 해당 산타는 C만큼의 점수를 얻게 된다.
        				santa[map[nextRR][nextRC]].point += C;
            			santa[map[nextRR][nextRC]].isSleep = turn+1;
        				
            			// 루돌프와 부딪힌 산타가 밀려난 위치
                		int nextSR = point[targetP].r+C*drr[targetD];
                		int nextSC = point[targetP].c+C*drc[targetD];
                			
                		// 산타가 밀려나서 탈락한 경우
                		if(nextSR<=0 || nextSR>N || nextSC<=0 || nextSC>N) {
                			santa[targetP].isOut=true;
                			outCnt++;
                			map[point[targetP].r][point[targetP].c]=-1;
                		}
                		else {
                    		// 또 다른 산타와 충돌한 경우
                    		if(map[nextSR][nextSC]>0) {
                    			collision(map[nextSR][nextSC],drr[targetD],drc[targetD]);
                    		}
                    		
                    		map[point[targetP].r][point[targetP].c] = -1;
                			map[nextSR][nextSC] = targetP;
                			point[targetP] = new Point(nextSR, nextSC);		
                		}      		
            		}

            		// 루돌프가 움직여서 충돌이 발생하든 발생하지 않았던 루돌프는 이동
            		map[point[0].r][point[0].c] = -1;
        			map[nextRR][nextRC] = 0;
        			point[0] = new Point(nextRR, nextRC);
        			
        		}
        		else { // 산타는 루돌프와 가장 가까워지는 방향으로 한 칸 돌진
        			// 기절한 산타라면 다음으로
        			if(santa[i].isSleep>=turn) continue;
        			if(santa[i].isOut) continue;
        			
        			int targetD = -1;
        			int curDist = getDist(point[0], point[i]);
        			for(int d=0; d<4; d++) {
        				int nextR = point[i].r+dsr[d];
        				int nextC = point[i].c+dsc[d];
        				if(nextR<=0 || nextR>N || nextC<=0 || nextC>N) continue;
        				if(map[nextR][nextC]>0) continue;
        				int dist = getDist(point[0], new Point(nextR, nextC));
        				if(dist<=curDist) {
        					curDist = dist;
        					targetD = d;
        				}
        			}
        			
        			
        			if(targetD==-1) continue; // 움직일 수 있는 칸이 없다면 움직이지 않는다.
        			
        			int nextR = point[i].r+dsr[targetD];
        			int nextC = point[i].c+dsc[targetD];
        			
        			// 루돌프와 충돌하는 경우
        			if(map[nextR][nextC]==0) {
        				// 산타가 움직여서 충돌이 발생한 경우 해당 산타는 D만큼의 점수를 얻게 된다.
        				santa[i].point += D;
        				santa[i].isSleep = turn+1;
        				
        				// 산타가 밀려난 위치
        				int nextSR = nextR - D*dsr[targetD];
        				int nextSC = nextC - D*dsc[targetD];
        				
        				// 탈락한 경우라면
        				if(nextSR<=0 || nextSR>N || nextSC<=0 || nextSC>N) {
        					santa[i].isOut=true;
                			outCnt++;
                			map[point[i].r][point[i].c]=-1;
                			continue;
        				}else {
            				// 또 산타와 충돌이 발생한 경우라면
        					if(map[nextSR][nextSC]>0) {
        						collision(map[nextSR][nextSC], -1*dsr[targetD], -1*dsc[targetD]);
        					}
        					
        					map[point[i].r][point[i].c] = -1;
                			map[nextSR][nextSC] = i;
                			point[i] = new Point(nextSR, nextSC);
                			continue;
        				}	
        			}
        		
        			// 루돌프와 충돌하든 충돌하지 않았든 어쨌든 움직임
        			map[point[i].r][point[i].c] = -1;
        			map[nextR][nextC] = i;
        			point[i] = new Point(nextR, nextC);		
        		}
        		        	
        	}
        	
        	for(int i=1; i<=P; i++) {
        		if(!santa[i].isOut) santa[i].point++;
        	}
        }
        
    	for(int i=1;i<=P; i++) {
    		System.out.print(santa[i].point+" ");
    	}
    	
    }
}