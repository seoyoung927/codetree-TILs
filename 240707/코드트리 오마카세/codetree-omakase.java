import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Collections;

public class Main {
    static int L, Q; //L: 초밥 벨트의 길이, Q: 명령의 수
    static ArrayList<Query> queries;
    static HashSet<String> names; //전체 사람 이름 집합
    static HashMap<String, ArrayList<Query>> customerQueries;
    static HashMap<String, Integer> entryTime; //사람이 입장한 시간
    static HashMap<String, Integer> exitTime; //사람이 퇴장한 시간
    static HashMap<String, Integer> position; //위치

    static class Query implements Comparable<Query>{
        int cmd, t, x, cnt;
        String name;
        public Query(int cmd, int t, int x, String name, int cnt){
            this.cmd = cmd;
            this.t = t;
            this.x = x;
            this.name = name;
            this.cnt = cnt;
        }
        @Override
        public int compareTo(Query o){
            if(this.t==o.t) return Integer.compare(this.cmd, o.cmd);
            return Integer.compare(this.t, o.t);
        }
        @Override
        public String toString(){
            return "cmd: "+cmd
                    +", t: "+t
                    +", x: "+x
                    +", name: "+name
                    +", cnt: "+cnt;
        }
    }

    public static void main(String[] args) throws Exception {
        //1. 입력
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        L = Integer.parseInt(st.nextToken());
        Q = Integer.parseInt(st.nextToken());

        //입력으로 주어지는 t 값은 모두 다르며, 오름차순으로 정렬되어 주어집니다.
        queries = new ArrayList<>();
        customerQueries = new HashMap<>();
        names = new HashSet<>();
        entryTime = new HashMap<>();
        exitTime = new HashMap<>();
        position = new HashMap<>();
        for(int q=0; q<Q; q++){
            st = new StringTokenizer(br.readLine());
            int cmd = Integer.parseInt(st.nextToken());
            int t = -1, x = -1, cnt = -1;
            String name = "";
            if(cmd==100){
                //2-1. 주방장의 초밥 만들기
                t = Integer.parseInt(st.nextToken());
                x = Integer.parseInt(st.nextToken());
                name = st.nextToken();
            }else if(cmd==200){
                t = Integer.parseInt(st.nextToken());
                x = Integer.parseInt(st.nextToken());
                name = st.nextToken();
                cnt = Integer.parseInt(st.nextToken());
            }else{
                t = Integer.parseInt(st.nextToken());
            }

            Query query = new Query(cmd, t, x, name, cnt);
            queries.add(query);
            if(cmd==100){
                if(!customerQueries.containsKey(name)) customerQueries.put(name, new ArrayList<>());
                customerQueries.get(name).add(query);
            }else if(cmd==200){
                names.add(name);
                entryTime.put(name,t);
                position.put(name,x);
            }
        }

        //2. 사람의 exitTime을 계산 
        for(String name : names){
            //해당 사람의 퇴장시간은 마지막으로 먹은 초밥 시간 중 가장 늦은 시간
            exitTime.put(name, 0);
            for(Query query : customerQueries.get(name)){
                int exitT = 0;
                if(query.t < entryTime.get(name)){
                    //초밥이 사람보다 먼저 등장하였다면
                    int sushiPos = (query.x + (entryTime.get(name) - query.t)) % L;
                    int waitTime = (position.get(name) - sushiPos + L) % L;
                    exitT = entryTime.get(name) + waitTime;
                }else{
                    //사람이 초밥보다 먼저 등장하였다면
                    int waitTime = (position.get(name) - query.x + L) % L;
                    exitT = query.t + waitTime;
                }

                //초밥이 사라지는 가장 늦은 시간 update
                exitTime.put(name, Math.max(exitTime.get(name), exitT));

                queries.add(new Query(111, exitT, -1, name, -1));
            }
        }

        for(String name : names) queries.add(new Query(222, exitTime.get(name), -1, name, -1));
    
        //3. 시간 순으로 정렬
        Collections.sort(queries);
        
        //System.out.println(queries);
        int pNum = 0, sNum = 0;
        for(Query query : queries){
            if(query.cmd == 100) sNum++;
            if(query.cmd == 111) sNum--;
            if(query.cmd == 200) pNum++;
            if(query.cmd == 222) pNum--;
            if(query.cmd == 300) System.out.println(pNum+" "+sNum);
        }
    }
}