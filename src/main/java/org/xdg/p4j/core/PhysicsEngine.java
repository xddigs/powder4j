package org.xdg.p4j.core;

import org.xdg.p4j.data.ElementID;

/**
 * TODO javadoc
 */
public class PhysicsEngine {

    public static void updateBody(Body body, World world, float dt) {
        body.update(dt);

        float cos = (float) Math.cos(body.angle);
        float sin = (float) Math.sin(body.angle);
        
        int centerX = body.maskWidth / 2;
        int centerY = body.maskHeight / 2;

        boolean colliding = false;
        float normalX = 0;
        float normalY = 0;

        for (int ly = 0; ly < body.maskHeight; ly++) {
            for (int lx = 0; lx < body.maskWidth; lx++) {
                byte localPixel = body.pixels[ly * body.maskWidth + lx];
                if (localPixel == ElementID.EMPTY.getId()) continue;

                int relX = lx - centerX;
                int relY = ly - centerY;

                int wx = Math.round(body.x + (relX * cos - relY * sin));
                int wy = Math.round(body.y + (relX * sin + relY * cos));

                if (wx < 0 || wx >= world.getWidth() || wy >= world.getHeight()) {
                    colliding = true;
                    normalY = -1;
                } else if (wy >= 0) {
                    byte worldPixel = world.getGrid()[wy * world.getWidth() + wx];
                    if (worldPixel != ElementID.EMPTY.getId()) {
                        colliding = true;
                        
                        normalX += (wx < body.x) ? 1 : -1;
                        normalY += (wy > body.y) ? -1 : 1;

                        body.angularVelocity += (wx - body.x) * body.vy *
                                Constants.BODY_ANGULAR_VEL_OFFSET;
                    }
                }
            }
        }

        if (colliding) {
            body.vy *= -0.2f;
            body.vx *= 0.8f;

            if (Math.abs(body.vy) < 0.2f && Math.abs(body.vx) < 0.2f) {
                body.vy = 0;
                body.vx = 0;
                body.angularVelocity = 0;
                body.isSettled = true;
            } else {
                body.y += normalY * 0.5f;
                body.x += normalX * 0.5f;
            }
        }
    }
}