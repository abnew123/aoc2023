import java.util.*;

class G{String s(boolean p,String I){long[] a=new long[1000];int z=0,i=0;String o=p?"23456789TJQKA":"J23456789TQKA";var S=I.split("\\s+");for(;i<S.length;){String h=S[i++];int[] c=new int[13];long k=0,b=Long.parseLong(S[i++]);int j=0;for(char x:h.toCharArray()){int v=o.indexOf(x);k=k*13+v;if(p|x!=74)c[v]++;else j++;}int m=0,n=0;for(int v:c)if(v>m){n=m;m=v;}else if(v>n)n=v;m+=j;a[z++]=((2*m+(n>1?1:0))*371293+k)<<32|b;}java.util.Arrays.sort(a,0,z);long r=0;for(i=0;i<z;)r+=(long)(i+1)*(int)a[i++];return r+"";}}
