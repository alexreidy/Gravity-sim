public class Orbiter {
	double radius, x, y;
	double xVelocity;
	double yVelocity;
	double theta;
	
	boolean fixed;
	
	public Orbiter(double x, double y, double radius) {
		this.x = x;
		this.y = y;
		this.radius = radius;
	}
	
	public Orbiter() {
		x = Gravity.rin(Gravity.W);
		y = Gravity.rin(Gravity.H);
		radius = Gravity.rin(3);
		xVelocity = Gravity.rsign(Gravity.rin(8));
		yVelocity = Gravity.rsign(Gravity.rin(8));
		theta = Gravity.rin(2 * Math.PI);
	}
	
}
