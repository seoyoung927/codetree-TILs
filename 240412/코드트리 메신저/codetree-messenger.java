import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static final int MAX_N = 100001;
    public static final int MAX_D = 22;

    public static int N,Q;
    public static int[] a = new int[MAX_N];
    public static int[] p = new int[MAX_N];
    public static int[] val = new int[MAX_N];
    public static boolean[] noti = new boolean[MAX_N];
    public static int[][] nx = new int[MAX_N][MAX_D];

    // 채팅의 알림 상태를 토글합니다.
    public static void toggle_noti(int chat) {
        if (noti[chat]) {
            int cur = p[chat];
            int num = 1;
            // 상위 채팅으로 이동하며 noti 값에 따라 nx와 val 값을 갱신합니다.
            while (cur != 0) {
                for (int i = num; i <= 21; i++) {
                    val[cur] += nx[chat][i];
                    if (i > num) nx[cur][i - num] += nx[chat][i];
                }
                if (noti[cur]) break;
                cur = p[cur];
                num++;
            }
            noti[chat] = false;
        } else {
            int cur = p[chat];
            int num = 1;
            // 상위 채팅으로 이동하며 noti 값에 따라 nx와 val 값을 갱신합니다.
            while (cur != 0) {
                for (int i = num; i <= 21; i++) {
                    val[cur] -= nx[chat][i];
                    if (i > num) nx[cur][i - num] -= nx[chat][i];
                }
                if (noti[cur]) break;
                cur = p[cur];
                num++;
            }
            noti[chat] = true;
        }
    }


        // 채팅의 권한의 크기를 변경합니다.
    public static void change_power(int chat, int power) {
        int bef_power = a[chat];
        power = Math.min(power, 20);  // 권한의 크기를 20으로 제한합니다.
        a[chat] = power;

        nx[chat][bef_power]--;
        if (!noti[chat]) {
            int cur = p[chat];
            int num = 1;
            // 상위 채팅으로 이동하며 nx와 val 값을 갱신합니다.
            while (cur != 0) {
                if (bef_power >= num) val[cur]--;
                if (bef_power > num) nx[cur][bef_power - num]--;
                if (noti[cur]) break;
                cur = p[cur];
                num++;
            }
        }

        nx[chat][power]++;
        if (!noti[chat]) {
            int cur = p[chat];
            int num = 1;
            // 상위 채팅으로 이동하며 nx와 val 값을 갱신합니다.
            while (cur != 0) {
                if (power >= num) val[cur]++;
                if (power > num) nx[cur][power - num]++;
                if (noti[cur]) break;
                cur = p[cur];
                num++;
            }
        }
    }

    public static void change_parent(int chat1, int chat2){
        boolean bef_noti1 = noti[chat1];
        boolean bef_noti2 = noti[chat2];
        
        if(!noti[chat1]) toggle_noti(chat1);
        if(!noti[chat2]) toggle_noti(chat2);

        int tmp = p[chat1];
        p[chat1] = p[chat2];
        p[chat2] = tmp;

        if(!bef_noti1) toggle_noti(chat1);
        if(!bef_noti2) toggle_noti(chat2);
    }

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        N = sc.nextInt();
        Q = sc.nextInt();
        
        for(int q=0; q<Q; q++){
            int cmd = sc.nextInt();
            if(cmd==100){
                for (int i = 1; i <= N; i++) {
                    p[i] = sc.nextInt();
                }
                for (int i = 1; i <= N; i++) {
                    a[i] = sc.nextInt();
                    // 채팅의 권한이 20을 초과하는 경우 20으로 제한합니다.
                    if (a[i] > 20) a[i] = 20;
                }
                
                // nx 배열과 val 값을 초기화합니다.
                for (int i = 1; i <= N; i++) {
                    int cur = i;
                    int x = a[i];
                    nx[cur][x]++;
                    // 상위 채팅으로 이동하며 nx와 val 값을 갱신합니다.
                    while (p[cur] != 0 && x != 0) {
                        cur = p[cur];
                        x--;
                        if (x != 0) nx[cur][x]++;
                        val[cur]++;
                    }
                }
            }else if(cmd==200){
                int c = sc.nextInt();
                toggle_noti(c);
            }else if(cmd==300){
                int c = sc.nextInt();
                int power = sc.nextInt();
                change_power(c,power);
            }else if(cmd==400){
                int c1 = sc.nextInt();
                int c2 = sc.nextInt();
                change_parent(c1,c2);
            }else if(cmd==500){
                int c = sc.nextInt();
                System.out.println(val[c]);
            }
        }

    }
}