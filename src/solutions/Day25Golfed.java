import java.util.*;
class Y{
 Map m=new HashMap();int g[][],n;
 String s(boolean p,String[]I){
  if(!p)return"Merry Christmas!";
  for(var L:I)for(var x:L.split(":? |:"))i(x);
  g=new int[n][n];
  for(var L:I){var a=L.split(":? |:");int x=i(a[0]);for(int j=1;j<a.length;){int y=i(a[j++]);g[x][y]++;g[y][x]++;}}
  var z=new int[n];Arrays.fill(z,1);
  for(int N=n;N>1;N--){var a=new boolean[n];var w=new int[n];int s=0,t=0;
   for(int k=N;k-->0;){s=t;t=-1;for(int j=0;j<n;j++)if(z[j]>0&&!a[j]&&(t<0||w[j]>w[t]))t=j;
    if(k>0){a[t]=true;for(int j=0;j<n;j++)w[j]+=g[t][j];}
    else{if(w[t]<4)return""+z[t]*(n-z[t]);for(int j=0;j<n;j++)g[j][s]=g[s][j]+=g[t][j];z[s]+=z[t];z[t]=0;}
   }
  }
  return"";
 }
 int i(String s){return(Integer)m.computeIfAbsent(s,k->n++);}
}
