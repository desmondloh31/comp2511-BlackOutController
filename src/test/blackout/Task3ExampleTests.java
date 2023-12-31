package blackout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import unsw.blackout.BlackoutController;
import unsw.blackout.Satellite;
import unsw.utils.Angle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

@TestInstance(value = Lifecycle.PER_CLASS)
public class Task3ExampleTests {
    @Test
    public void testElephantSatelliteMovement() {
        BlackoutController controller = new BlackoutController();
        controller.createSatellite("Satellite1", "ElephantSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(90));

        Satellite initialSatellite = controller.findSatelliteById("Satellite1");
        Angle initialSatellitePosition = initialSatellite.getPosition();

        controller.simulate(5);

        Satellite updatedSatellite = controller.findSatelliteById("Satellite1");
        Angle updatedSatellitePosition = updatedSatellite.getPosition();

        assertNotEquals(initialSatellitePosition, updatedSatellitePosition);

    }

    @Test
    public void testElephantSatelliteMovementSpeed() {
        BlackoutController controller = new BlackoutController();
        Angle initialPosition = Angle.fromDegrees(90);
        controller.createSatellite("Satellite1", "ElephantSatellite", 1000 + RADIUS_OF_JUPITER, initialPosition);
        controller.simulate(5);

        Satellite updatedSatellite = controller.findSatelliteById("Satellite1");
        double observedSpeedPerMinute = (100.24441423972662 - initialPosition.toDegrees()) / 5;
        Angle expectedPosition = Angle.fromDegrees(initialPosition.toDegrees() + 5 * observedSpeedPerMinute);
        assertEquals(expectedPosition, updatedSatellite.getPosition());
    }

    @Test
    public void testElephantSatelliteLooping() {
        // Create an ElephantSatellite
        BlackoutController controller = new BlackoutController();
        Angle initialPosition = Angle.fromDegrees(350);
        controller.createSatellite("Satellite1", "ElephantSatellite", 1000 + RADIUS_OF_JUPITER, initialPosition);
        controller.simulate(15);

        Satellite updatedSatellite = controller.findSatelliteById("Satellite1");
        double updatedPosition = updatedSatellite.getPosition().toDegrees();
        assertTrue(updatedPosition >= 360 || updatedPosition < initialPosition.toDegrees());
    }
}
