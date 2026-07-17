import java.util.*;
class Y{
 Map m=new HashMap();int[][]g,r;int n,c,q[];
 String s(boolean p,String[]I){
  if(!p)return"Merry Christmas!";
  for(var L:I)for(var x:L.split(":? "))i(x);
  g=new int[n][n];
  for(var L:I){var a=L.split(":? ");int x=i(a[0]);for(int j=1,y;j<a.length;g[x][y]=g[y][x]=1)y=i(a[j++]);}
  for(int t=n;t-->1;){r=new int[n][];for(int j=n;j-->0;)r[j]=g[j].clone();
   for(int z=4;z-->0;){q=new int[n];c=0;if(!f(0,t))return""+c*(n-c);}
  }
  return"";
 }
 boolean f(int x,int t){if(x==t)return true;q[x]=1;c++;for(int j=n;j-->0;)if(q[j]<1&r[x][j]>0&&f(j,t)){r[x][j]--;r[j][x]++;return true;}return false;}
 int i(String s){return(int)m.computeIfAbsent(s,k->n++);}
}
