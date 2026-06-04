package com.zcard.feature.cardeditor.transform

import kotlin.math.*

data class Quaternion(val x: Float, val y: Float, val z: Float, val w: Float) {

    // Quaternion 곱셈 (Hamilton product)
    operator fun times(q: Quaternion) = Quaternion(
        w*q.x + x*q.w + y*q.z - z*q.y,
        w*q.y - x*q.z + y*q.w + z*q.x,
        w*q.z + x*q.y - y*q.x + z*q.w,
        w*q.w - x*q.x - y*q.y - z*q.z
    )

    // Unity YXZ 역변환 (Quaternion → Euler degrees)
    fun toEuler(): Rotation {
        val qx = x.toDouble(); val qy = y.toDouble()
        val qz = z.toDouble(); val qw = w.toDouble()

        val sinX = 2.0 * (qw * qx - qy * qz)
        return if (abs(sinX) >= 0.9999) {
            // 짐벌락: X축이 ±90°일 때 Y, Z를 분리할 수 없어 Z=0으로 고정
            Rotation(
                x = (90.0 * sign(sinX)).toFloat(),
                y = Math.toDegrees(atan2(2.0 * (qy * qw - qx * qz), 1.0 - 2.0 * (qy * qy + qz * qz))).toFloat(),
                z = 0f
            )
        } else {
            Rotation(
                x = Math.toDegrees(asin(sinX)).toFloat(),
                y = Math.toDegrees(atan2(2.0 * (qx * qz + qw * qy), 1.0 - 2.0 * (qx * qx + qy * qy))).toFloat(),
                z = Math.toDegrees(atan2(2.0 * (qx * qy + qw * qz), 1.0 - 2.0 * (qx * qx + qz * qz))).toFloat()
            )
        }
    }

    companion object {
        val identity = Quaternion(0f, 0f, 0f, 1f)

        // Unity의 Quaternion.Euler(x, y, z) — 내부 순서: Z → X → Y
        fun euler(rotation: Rotation): Quaternion {
            val xr = Math.toRadians(rotation.x.toDouble())
            val yr = Math.toRadians(rotation.y.toDouble())
            val zr = Math.toRadians(rotation.z.toDouble())
            val cx = cos(xr / 2).toFloat(); val sx = sin(xr / 2).toFloat()
            val cy = cos(yr / 2).toFloat(); val sy = sin(yr / 2).toFloat()
            val cz = cos(zr / 2).toFloat(); val sz = sin(zr / 2).toFloat()
            return Quaternion(
                cx*sy*sz + sx*cy*cz,
                cx*sy*cz - sx*cy*sz,
                cx*cy*sz - sx*sy*cz,
                cx*cy*cz + sx*sy*sz
            )
        }
    }
}
