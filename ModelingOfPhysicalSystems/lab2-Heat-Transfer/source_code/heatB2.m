%Simulation with boundary conditions type 2

close all
clear all
clc

%Initial Parameters
a=0.2;
b=0.05;
h=0.002;
dx=0.01;
dy=0.01;
dt=0.1;
Nt=1000;
Nx=a/dx;
Ny=a/dy;
material='Cooper';
K=0;
Cw=0;
Rho=0;
P=100;
if strcmp(material,'Alumina')
    K=237;
    Cw=900;
    Rho=2700;
elseif strcmp(material,'Cooper')
    K=401;
    Cw=380;
    Rho=8920;
else
    K=58;
    Cw=450;
    Rho=7860;
end;  

T=zeros(Nx,Ny,Nt);
T(:,:,1)=20; %Initial Condition





for t=1:Nt-1

    for i=2:Nx-1
        for j=2:Ny-1
            if (~((i>=(0.075/dx) &&  i<=(Nx-0.075/dx)) && (j>=(0.075/dy) &&  j<=(Nx-0.075/dy) ) ) || t>=(1/dt) )
            T(i,j,t+1)=T(i,j,t)+(K*dt/Cw*Rho*dx*dx)*(T(i+1,j,t)-2*T(i,j,t)+T(i-1,j,t))+(K*dt/Cw*Rho*dy*dy)*(T(i,j+1,t)-2*T(i,j,t)+T(i,j-1,t));
            else
               if t<(1/dt)
                T(i,j,t+1)=T(i,j,t)+(K*dt/Cw*Rho*dx*dx)*(T(i+1,j,t)-2*T(i,j,t)+T(i-1,j,t))+(K*dt/Cw*Rho*dy*dy)*(T(i,j+1,t)-2*T(i,j,t)+T(i,j-1,t))+(P*dt)/(Cw*b*b*h*Rho);
               end;
            end;
        end;
    end;
T(1,1,t+1)=T(2,2,t+1);
T(Nx,Ny,t+1)=T(Nx-1,Ny-1,t+1);
T(1,Ny,t+1)=T(2,Ny-1,t+1);
T(Nx,1,t+1)=T(Nx-1,2,t+1);
T(1,:,t+1)=T(2,:,t+1);
T(Nx,:,t+1)=T(Nx-1,:,t+1);
T(:,1,t+1)=T(:,2,t+1);
T(:,Ny,t+1)=T(:,Ny-1,t+1);

end;

[XX YY]=meshgrid(dx:dx:a,dy:dy:a);
figure
for tt=1:t+1
%     subplot(1,2,1)
    val=-1;
    if tt>1
        val=sum(sum(T(:,:,tt)-T(:,:,tt-1)));
    end;
    
%     heatmap(T(:,:,tt));
%     title(['time=  ',num2str(tt*dt), ' (s) diff=',num2str(val)]);
%     xlabel(['x  [',num2str(dx), 'm]']);
%     ylabel(['y  [',num2str(dy), 'm]']);
   
   surf(XX,YY,T(:,:,tt));
   title(['time=  ',num2str(tt*dt), ' (s)']);
  
    xlabel(['x  [',num2str(dx), 'm]']);
    ylabel(['y  [',num2str(dy), 'm]']);
    zlabel('temperature [C]');
    axis([0 a 0 a 20 25]);
    
    pause(0.1);
    clf;
end;


title(['Simulation time=  ',num2str(t*dt), ' (s)']);
xlabel('x (m)');
ylabel('y (m)');
zlabel('Temperature (C)');
