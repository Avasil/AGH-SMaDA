global S as

Ts = 273;
Ta = 273;
as = 0.19;
initialS  = 1366;

earth_temperature = [];
atmosphere_temperature = [];

for i = round(0.8 * initialS):round(1.2*initialS)
    S = i;
    Xp = [Ts Ta];
    X = fsolve(@balance_equation, Xp);
    earth_temperature = [earth_temperature X(1)];
    atmosphere_temperature = [atmosphere_temperature X(2)];
end

figure;
plot(solar_constant, earth_temperature);
xlabel('Solar constant [W/m^2]');
ylabel('Mean Earth temperature [K]');

figure;
plot(solar_constant, atmosphere_temperature);
xlabel('Solar constant [W/m^2]');
ylabel('Mean Atmosphere temperature [K]');