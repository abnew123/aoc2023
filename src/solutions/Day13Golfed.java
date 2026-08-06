class M{
String s(boolean p,String[]I){long a=0;for(var b:String.join("\n",I).split("\n\n")){int c=(b+"\n").indexOf(10),r=(b.length()+1)/(c+1),i,j,d,x;for(int q=2,n=r,m=c,z=c+1;q-->0;n=c,m=r,z=1)for(i=n;i-->1;){for(x=d=0;d<i&d<n-i;d++)for(j=m;j-->0;)x+=b.charAt((i-d-1)*z+j*(c+2-z))^b.charAt((i+d)*z+j*(c+2-z));if(x==(p?0:13))a+=(q*99+1)*i;}}return a+"";}
}
