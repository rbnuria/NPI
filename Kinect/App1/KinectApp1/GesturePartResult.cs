using System;

namespace KinectSimpleGesture
{
    /// <summary>
    /// Enumerado para controlar la detección de gestos. Cuando una clase reconocedora reconozca determinado gesto,
    /// asignará el valor 'succeded'. En caso contrario, asignará el valor 'failed'
    /// </summary>
    public enum GesturePartResult
    {
        /// <summary>
        /// Gesture part failed.
        /// </summary>
        Failed,

        /// <summary>
        /// Gesture part succeeded.
        /// </summary>
        Succeeded
    }
}
