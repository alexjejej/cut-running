package com.cut.android.running.provider.resources
import nl.dionsegijn.konfetti.core.*
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size
import java.util.concurrent.TimeUnit

class Presets {
    companion object {
        fun festive(drawable: Shape.DrawableShape? = null): List<Party> {
            val party = Party(
                speed = 60f,
                maxSpeed = 125f,
                damping = 0.9f,
                angle = Angle.TOP,
                spread = 65,
                size = listOf(Size.SMALL, Size.LARGE, Size.LARGE),
                shapes = listOf(Shape.Square, Shape.Circle, drawable).filterNotNull(),
                timeToLive = 3000L,
                rotation = Rotation(),
                colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                emitter = Emitter(duration = 110, TimeUnit.MILLISECONDS).max(45),
                position = Position.Relative(0.5, 1.0)
            )

            return listOf(
                party,
                party.copy(
                    speed = 115f,
                    maxSpeed = 130f,
                    spread = 20,
                    emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(20),
                ),
                party.copy(
                    speed = 110f,
                    maxSpeed = 130f,
                    spread = 130,
                    emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(50),
                ),
                party.copy(
                    speed = 125f,
                    maxSpeed = 170f,
                    spread = 20,
                    emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(20),
                )
            )
        }

        fun explode(): List<Party> {
            return listOf(
                Party(
                    speed = 0f,
                    maxSpeed = 50f,
                    damping = 0.9f,
                    spread = 360,
                    colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                    emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
                    position = Position.Relative(0.5, 0.3)
                )
            )
        }

        fun parade(): List<Party> {
            val party = Party(
                speed = 15f,
                maxSpeed = 90f,
                damping = .9f,
                angle = Angle.RIGHT - 55,
                spread = Spread.SMALL,
                colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                emitter = Emitter(duration = 5, TimeUnit.SECONDS).perSecond(45),
                position = Position.Relative(0.0, 0.5)
            )

            return listOf(
                party,
                party.copy(
                    angle = party.angle - 75, // flip angle from right to left
                    position = Position.Relative(1.0, 0.5)
                ),

            )

        }

        fun rain(): List<Party> {
            return listOf(
                Party(
                    speed = 0f,
                    maxSpeed = 15f,
                    damping = 0.9f,
                    angle = Angle.BOTTOM,
                    spread = Spread.ROUND,
                    colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                    emitter = Emitter(duration = 5, TimeUnit.SECONDS).perSecond(100),
                    position = Position.Relative(0.0, 0.0).between(Position.Relative(1.0, 0.0))
                )
            )
        }
    }
}