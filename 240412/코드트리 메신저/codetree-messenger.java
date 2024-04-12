import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Arrays;

public class Main {
    static int N,Q; //N: 채팅방의 수, Q: 명령의 수
    static int[] p;
    static int[] a;
    static boolean[] noti;
    static int[][] nx;
    static int[] val;

    public static void toggle_noti(int chat){
        if(noti[chat]){
            //현재 알림이 켜져 있다면
            int cur = p[chat];
            int num = 1; //depth 차이
            while(cur!=0){
                for(int i=num; i<N+1; i++){
                    val[cur]-=nx[chat][i];
                    nx[cur][i-num]-=nx[chat][i];
                }
                if(!noti[cur]) break;
                cur=p[cur];
                num++;
            }
            noti[chat]=false;
        }else{
            //현재 알림이 꺼져 있다면
            int cur = p[chat];
            int num = 1;
            while(cur!=0){
                for(int i=num; i<N+1; i++){
                    val[cur]+=nx[chat][i];
                    nx[cur][i-num]+=nx[chat][i];
                }
                if(!noti[cur]) break;
                cur=p[cur];
                num++;
            }
            noti[chat]=true;
        }
    }

    public static void change_power(int chat, int power){
        int bef_power = a[chat];
        power = Math.min(power, 20);
        a[chat] = power;

        nx[chat][bef_power]--;
        if(noti[chat]){
            int cur = p[chat];
            int num = 1;
            //상위 채팅으로 이동하며 nx와 val 값을 갱신
            while(cur!=0){
                if(bef_power>=num) val[cur]--;
                if (bef_power > num) nx[cur][bef_power - num]--;
                if(!noti[cur]) break;
                cur=p[cur];
                num++;
            }
        }
        nx[chat][power]++;
        if (noti[chat]) {
            int cur = p[chat];
            int num = 1;
            // 상위 채팅으로 이동하며 nx와 val 값을 갱신합니다.
            while (cur != 0) {
                if (power >= num) val[cur]++;
                if (power > num) nx[cur][power - num]++;
                if (!noti[cur]) break;
                cur = p[cur];
                num++;
            }
        }
    }

    public static void change_parent(int chat1, int chat2){
        boolean bef_noti1 = noti[chat1];
        boolean bef_noti2 = noti[chat2];
        
        if(noti[chat1]) toggle_noti(chat1);
        if(noti[chat2]) toggle_noti(chat2);

        int tmp = p[chat1];
        p[chat1] = p[chat2];
        p[chat2] = tmp;

        if(bef_noti1) toggle_noti(chat1);
        if(bef_noti1) toggle_noti(chat2);
    }

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        Q = Integer.parseInt(st.nextToken());
        
        for(int q=0; q<Q; q++){
            st = new StringTokenizer(br.readLine());
            int cmd = Integer.parseInt(st.nextToken());
            if(cmd==100){
                p = new int[N+1];
                a = new int[N+1];
                for(int i=1; i<N+1; i++) p[i]=Integer.parseInt(st.nextToken());
                for(int i=1; i<N+1; i++) {
                    a[i]=Integer.parseInt(st.nextToken());
                    if(a[i]>20) a[i]=20; //채팅의 권한이 20을 초과하는 경우 20으로 제한
                }

                noti = new boolean[N+1];
                Arrays.fill(noti,true);
                nx = new int[N+1][20];
                val = new int[N+1];
                for(int i=1; i<N+1; i++){
                    int cur = i;
                    int x = a[cur];
                    nx[cur][x]++;
                    cur = p[cur];
                    x--;
                    while(cur!=0 && x>=0){
                        if(x!=0) nx[cur][x]++;
                        val[cur]++;
                        cur=p[cur];
                        x--;
                    }
                }
            }else if(cmd==200){
                int c = Integer.parseInt(st.nextToken());
                toggle_noti(c);
            }else if(cmd==300){
                int c = Integer.parseInt(st.nextToken());
                int power = Integer.parseInt(st.nextToken());
                change_power(c,power);
            }else if(cmd==400){
                int c1 = Integer.parseInt(st.nextToken());
                int c2 = Integer.parseInt(st.nextToken());
                change_parent(c1,c2);
            }else if(cmd==500){
                int c = Integer.parseInt(st.nextToken());
                System.out.println(val[c]);
            }
        }

    }
}