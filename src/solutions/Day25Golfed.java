class Y{
 int r[],n,c,q[];
 String s(boolean p,String[]I){
  var m="";
  for(var L:I)for(var x:L.split(":? "))if(!m.contains(x))m+=x+n++%2;
  var g=new int[n*n];
  for(var L:I){var a=L.split(":? ");c=m.indexOf(a[0])/4;for(int j=1,y;j<a.length;)g[c*n+(y=m.indexOf(a[j++])/4)]=g[y*n+c]=1;}
  for(int t=n;t-->1;){r=g.clone();
   for(int z=4;z-->0;){q=new int[n];if(f(c=0,t)<1)return""+c*(n-c);}
  }
  return"";
 }
 int f(int x,int t){if(x==t)return 1;q[x]=++c;for(int j=n;j-->0;)if(q[j]<1&r[x*n+j]>0&&f(j,t)>0){r[x*n+j]--;return++r[j*n+x];}return 0;}
}
