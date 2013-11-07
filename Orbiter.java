public class Orbiter {

    boolean fixed;
    double radius, x, y;
    double xVelocity;
    double yVelocity;
    double theta;

    public Orbiter(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }
    
    public Orbiter() {
        x = GravitySim.rin(GravitySim.W);
        y = GravitySim.rin(GravitySim.H);
        radius = GravitySim.rin(3);
        xVelocity = GravitySim.rsign(GravitySim.rin(8));
        yVelocity = GravitySim.rsign(GravitySim.rin(8));
        theta = GravitySim.rin(2 * Math.PI);
    }
    
}
