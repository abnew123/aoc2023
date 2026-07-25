class Y{
 int r[],n,c,q[],z,t;
 String s(boolean p,String[]I){
  var m="";
  for(var L:I)for(var x:L.split(":? "))m+=m.contains(x)?"":x+n++%2;
  var g=new int[n*n];
  for(var L:I){c=n;for(var x:L.split(":? "))g[(z=m.indexOf(x)/4)*n+(c=c<n?c:z)]=g[c*n+z]=1;}
  for(t=n;t-->1;)for(r=g.clone(),z=4;z-->0;){q=new int[n];if(!f(c=0))return""+c*(n-c);}
  return"";
 }
 boolean f(int x){q[x]=c+++6;for(int j=n;j-->0;)if(x!=t&r[x*n+j]>q[j]&&f(j))return r[j*n+x]+++r[x*n+j]-->0;return x==t;}
}
