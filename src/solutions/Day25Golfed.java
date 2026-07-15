import java.util.*;
class Y{
 Map m=new HashMap();int g[][],r[][],n,q[];
 String s(boolean p,String[]I){
  if(!p)return"Merry Christmas!";
  for(var L:I)for(var x:L.split(":? |:"))i(x);
  g=new int[n][n];
  for(var L:I){var a=L.split(":? |:");int x=i(a[0]);for(int j=1;j<a.length;){int y=i(a[j++]);g[x][y]++;g[y][x]++;}}
  for(int t=n;t-->1;){r=new int[n][];for(int j=n;j-->0;)r[j]=g[j].clone();
   for(int z=4;z-->0;){q=new int[n];if(!f(0,t)){int c=0;for(int x:q)c+=x;return""+c*(n-c);}}
  }
  return"";
 }
 boolean f(int x,int t){if(x==t)return true;q[x]=1;for(int j=n;j-->0;)if(q[j]<1&&r[x][j]>0&&f(j,t)){r[x][j]--;r[j][x]++;return true;}return false;}
 int i(String s){return(int)m.computeIfAbsent(s,k->n++);}
}
