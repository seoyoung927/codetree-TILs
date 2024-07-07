import java.util.*;
import java.io.*;

public class Main {
    static final int MAX_NAME = 15055;

    static int L; //L: 초밥 벨트의 길이
    static int Q; //Q: 명령의 수
    static Map<String, Integer> address;
    static int addIdx;
    static int[][] sushi;
    static int[] customer;
    static int[] remainS;
    static int[] remainC;
    static int prevT;

    //시각 t에 위치 x의 고정 위치 return
    public static int getFixedPos(int t, int x){
        int ret = x-t;
        if(ret<0){
            int tmp = ret*-1;
            int ans = tmp / 5;
            ret += (ans+1)*L;
        }
        ret %= L;
        return ret;
    }

    //시각 t의 고정 위치 x의 동적 위치 return
    public static int getDynamicPos(int t, int x){
        int ret = x+t;
        ret %= L;
        return ret;
    }

    public static void main(String[] args) throws IOException{
        //1. 입력 및 초기화
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        L = Integer.parseInt(st.nextToken());
        Q = Integer.parseInt(st.nextToken());

        address = new HashMap<>();
        addIdx = 0;
        sushi = new int[MAX_NAME][L];
        customer = new int[MAX_NAME];
        remainS = new int[MAX_NAME];
        remainC = new int[MAX_NAME];
        prevT = 0;

        for(int c=0; c<Q; c++){
            st = new StringTokenizer(br.readLine());
            int cmd = Integer.parseInt(st.nextToken());
            //System.out.println(cmd);

            if(cmd==100){
                //주방장의 초밥 만들기
                //100 t x name
                //주방장이 시각 t에 위치 x 앞에 있는 벨트 위에 name 이름의 회전 초밥을 하나 올려놓는다.
                int t = Integer.parseInt(st.nextToken());
                int x = Integer.parseInt(st.nextToken());
                String name = st.nextToken();

                if(!address.containsKey(name)) address.put(name, addIdx++);

                for(int time=prevT; time<t; time++){

                    for(int i=0; i<addIdx; i++){
                        if(remainS[i]==0 || remainC[i]==0) continue;

                        //0~L까지 위치 계산
                        for(int j=0; j<L; j++){
                            if(sushi[i][j]==0) continue;

                            int dynamicPos = getDynamicPos(time, j);
                            if(dynamicPos==customer[i]){
                                if(sushi[i][j]>=remainC[i]){
                                    remainS[i]-=sushi[i][j];
                                    sushi[i][j]-=remainC[i];
                                    remainC[i]=0;
                                }else if(remainC[i]>=sushi[i][j]){
                                    remainC[i]-=sushi[i][j];
                                    remainS[i]-=sushi[i][j];
                                    sushi[i][j]=0;
                                }
                            }
                        }
                    }
                }
                prevT = t-1;

                int idx = address.get(name);
                int pos = getFixedPos(t,x);
                sushi[idx][pos]++;
                remainS[idx]++;

//                System.out.println("t= "+t);
//                System.out.println("sushi");
//                for(int i=0; i<MAX_NAME; i++) System.out.println(Arrays.toString(sushi[i]));
//                System.out.println("customer");
//                System.out.println(Arrays.toString(customer));
//                System.out.println("remainS");
//                System.out.println(Arrays.toString(remainS));
//                System.out.println("remainC");
//                System.out.println(Arrays.toString(remainC));
//                System.out.println();
            }else if(cmd==200){
                //손님 입장
                //200 t x name n
                //이름이 name인 사람이 시각 t에 위치 x에 있는 의자로 가서 앉은 뒤 n개의 초밥을 먹을 때까지 기다리게 됨
                int t = Integer.parseInt(st.nextToken());
                int x = Integer.parseInt(st.nextToken());
                String name = st.nextToken();
                int n = Integer.parseInt(st.nextToken());

                if(!address.containsKey(name)) address.put(name, addIdx++);

                for(int time=prevT; time<t; time++){
                    //System.out.println("time= "+time);
                    for(int i=0; i<addIdx; i++){
                        //System.out.println("i= "+i);
                        if(remainS[i]==0 || remainC[i]==0) continue;

                        //0~L까지 위치 계산
                        for(int j=0; j<L; j++){
                            if(sushi[i][j]==0) continue;

                            int dynamicPos = getDynamicPos(time, j);
                            //System.out.println(dynamicPos);
                            if(dynamicPos==customer[i]){
                                if(sushi[i][j]>=remainC[i]){
                                    remainS[i]-=sushi[i][j];
                                    sushi[i][j]-=remainC[i];
                                    remainC[i]=0;
                                }else if(remainC[i]>=sushi[i][j]){
                                    remainC[i]-=sushi[i][j];
                                    remainS[i]-=sushi[i][j];
                                    sushi[i][j]=0;
                                }
                            }
                        }
                    }
                }
                prevT = t-1;

                int idx = address.get(name);
                customer[idx]=x;
                remainC[idx]=n;


//                System.out.println("t= "+t);
//                System.out.println("sushi");
//                for(int i=0; i<MAX_NAME; i++) System.out.println(Arrays.toString(sushi[i]));
//                System.out.println("customer");
//                System.out.println(Arrays.toString(customer));
//                System.out.println("remainS");
//                System.out.println(Arrays.toString(remainS));
//                System.out.println("remainC");
//                System.out.println(Arrays.toString(remainC));
//                System.out.println();
            }else if(cmd==300){
                //사진 촬영
                //300 t
                int t = Integer.parseInt(st.nextToken());

                for(int time=prevT; time<=t; time++){
                    //System.out.println("time= "+time);
                    for(int i=0; i<addIdx; i++){
                        //System.out.println("i= "+i);
                        if(remainS[i]==0 || remainC[i]==0) continue;

                        //0~L까지 위치 계산
                        for(int j=0; j<L; j++){
                            if(sushi[i][j]==0) continue;

                            int dynamicPos = getDynamicPos(time, j);
                            //System.out.println(dynamicPos);
                            if(dynamicPos==customer[i]){
                                if(sushi[i][j]>=remainC[i]){
                                    remainS[i]-=sushi[i][j];
                                    sushi[i][j]-=remainC[i];
                                    remainC[i]=0;
                                }else if(remainC[i]>=sushi[i][j]){
                                    remainC[i]-=sushi[i][j];
                                    remainS[i]-=sushi[i][j];
                                    sushi[i][j]=0;
                                }
                            }
                        }
                    }
                }
                prevT = t;

//                System.out.println("t= "+t);
//                System.out.println("sushi");
//                for(int i=0; i<MAX_NAME; i++) System.out.println(Arrays.toString(sushi[i]));
//                System.out.println("customer");
//                System.out.println(Arrays.toString(customer));
//                System.out.println("remainS");
//                System.out.println(Arrays.toString(remainS));
//                System.out.println("remainC");
//                System.out.println(Arrays.toString(remainC));
//                System.out.println();
//                System.out.println("==========================");
                int sushiCnt = 0;
                int customerCnt = 0;
                for(int i=0; i<addIdx; i++){
                    if(remainS[i]>0) sushiCnt+=remainS[i];
                    if(remainC[i]>0) customerCnt++;
                }
                System.out.println(customerCnt+" "+sushiCnt);
//                System.out.println("==========================");
            }
        }
    }
}