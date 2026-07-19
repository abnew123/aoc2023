import java.util.*;
class Y{
 Map m=new HashMap();int g[],r[],n,c,q[];
 String s(boolean p,String[]I){
  for(var L:I)for(var x:L.split(":? "))m.computeIfAbsent(x,k->n++);
  g=new int[n*n];
  for(var L:I){var a=L.split(":? ");c=(int)m.get(a[0]);for(int j=1,y;j<a.length;)g[c*n+(y=(int)m.get(a[j++]))]=g[y*n+c]=1;}
  for(int t=n;t-->1;){r=g.clone();
   for(int z=4;z-->0;){q=new int[n];if(f(c=0,t)<1)return""+c*(n-c);}
  }
  return"";
 }
 int f(int x,int t){if(x==t)return 1;q[x]=++c;for(int j=n;j-->0;)if(q[j]<1&r[x*n+j]>0&&f(j,t)>0){r[x*n+j]--;return++r[j*n+x];}return 0;}
}
