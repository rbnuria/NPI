using Microsoft.Kinect;
using System;

/// <summary>
/// Fichero GestureSegments.
/// Representa distintas clases que contienen diferentes segmentos de gesto que se pueden agrupar formando un gesto completo.
/// </summary>
namespace KinectSimpleGesture
{
    /// <summary>
    /// Interfaz para representar los distintos segmentos que definen un gesto.
    /// </summary>
    public interface IGestureSegment
    {
        /// <summary>
        /// Actualiza el gesto actual con la información recibida.
        /// </summary>
        /// <param name="skeleton">Skeleton detectado.</param>
        /// <returns>Valor GesturePartResult indicando si el segmento se ha realizado o no.</returns>
        GesturePartResult Update(Skeleton skeleton);
    }

    /// <summary>
    /// Clase WaveSegmentR1
    /// Representa la posición brazo derecho a la derecha utilizada para el gesto de desplazamiento del brazo.
    /// </summary>
    public class WaveSegmentR1 : IGestureSegment
    {
        
        public GesturePartResult Update(Skeleton skeleton)
        {
            // Mano por encima del hombro.
            if (skeleton.Joints[JointType.HandRight].Position.Y > skeleton.Joints[JointType.ShoulderRight].Position.Y)
            {
                // Mano a la derecha del hombro.
                
                if (skeleton.Joints[JointType.HandRight].Position.X > skeleton.Joints[JointType.ShoulderRight].Position.X + 0.1)
                {
                    
                    return GesturePartResult.Succeeded;
                }
            }

            // No completado.
            return GesturePartResult.Failed;
        }
    }

    /// <summary>
    /// Clase WaveSegmentR2
    /// Representa la posición brazo derecho a la izquierda utilizada para el gesto de desplazamiento del brazo.
    /// </summary>
    public class WaveSegmentR2 : IGestureSegment
    {
        
        public GesturePartResult Update(Skeleton skeleton)
        {
            // Mano por encima del hombro
            if (skeleton.Joints[JointType.HandRight].Position.Y > skeleton.Joints[JointType.ShoulderRight].Position.Y)
            {
                // Mano a la izquierda del hombro
                if (skeleton.Joints[JointType.HandRight].Position.X < skeleton.Joints[JointType.ShoulderRight].Position.X - 0.12)
                {
                    return GesturePartResult.Succeeded;
                }
            }

            // No completado.
            return GesturePartResult.Failed;
        }
    }

    /// <summary>
    /// Clase WaveSegmentL1
    /// Representa la posición brazo izquierdo a la izquierda utilizada para el gesto de desplazamiento del brazo.
    /// </summary>
    public class WaveSegmentL1 : IGestureSegment
    {
        
        public GesturePartResult Update(Skeleton skeleton)
        {
            // Mano por encima del hombro
            if (skeleton.Joints[JointType.HandLeft].Position.Y > skeleton.Joints[JointType.ShoulderLeft].Position.Y)
            {
                // Mano por la izquierda del hombro
                if (skeleton.Joints[JointType.HandLeft].Position.X < skeleton.Joints[JointType.ShoulderLeft].Position.X - 0.1)
                {
                    return GesturePartResult.Succeeded;
                }
            }

            return GesturePartResult.Failed;
        }
    }

    /// <summary>
    /// Clase WaveSegmentL2
    /// Representa la posición brazo izquierdo a la derecha utilizada para el gesto de desplazamiento del brazo.
    /// </summary>
    public class WaveSegmentL2 : IGestureSegment
    {
       
        public GesturePartResult Update(Skeleton skeleton)
        {
            // Mano por encima del hombro
            if (skeleton.Joints[JointType.HandLeft].Position.Y > skeleton.Joints[JointType.ShoulderLeft].Position.Y)
            {
                // Mano a la derecha del hombro
                if (skeleton.Joints[JointType.HandLeft].Position.X > skeleton.Joints[JointType.ShoulderLeft].Position.X + 0.12)
                {
                    return GesturePartResult.Succeeded;
                }
            }

            return GesturePartResult.Failed;
        }
    }

    /// <summary>
    /// ClapSegment1
    /// Representa la posición inicial previa al gesto de la palmada.
    /// </summary>
    public class ClapSegment1 : IGestureSegment
    {
        public GesturePartResult Update(Skeleton skeleton)
        {
            // Manos por debajo del hombro y distancia entre manos sobre cada eje superior a un umbral. 
            if (skeleton.Joints[JointType.HandRight].Position.Y < skeleton.Joints[JointType.ShoulderRight].Position.Y && 
                skeleton.Joints[JointType.HandLeft].Position.Y < skeleton.Joints[JointType.ShoulderLeft].Position.Y && 
                Math.Abs(skeleton.Joints[JointType.HandLeft].Position.X - skeleton.Joints[JointType.HandRight].Position.X) > 0.2 &&
                Math.Abs(skeleton.Joints[JointType.HandLeft].Position.Y - skeleton.Joints[JointType.HandRight].Position.Y) > 0.2 &&
                Math.Abs(skeleton.Joints[JointType.HandLeft].Position.Z - skeleton.Joints[JointType.HandRight].Position.Z) > 0.2
                )
            {
                return GesturePartResult.Succeeded;
            }
            return GesturePartResult.Failed;
        }
    }

    /// <summary>
    /// ClapSegment1
    /// Representa la posición final para el gesto de la palmada.
    /// </summary>
    public class ClapSegment2 : IGestureSegment
    {
        public GesturePartResult Update(Skeleton skeleton)
        {
            // Manos por debajo del hombro y distancia entre manos sobre cada eje inferior a un umbral.
            if (skeleton.Joints[JointType.HandRight].Position.Y < skeleton.Joints[JointType.ShoulderRight].Position.Y &&
                skeleton.Joints[JointType.HandLeft].Position.Y < skeleton.Joints[JointType.ShoulderLeft].Position.Y &&
                Math.Abs(skeleton.Joints[JointType.HandLeft].Position.X - skeleton.Joints[JointType.HandRight].Position.X) < 0.1 &&
                Math.Abs(skeleton.Joints[JointType.HandLeft].Position.Y - skeleton.Joints[JointType.HandRight].Position.Y) < 0.1 &&
                Math.Abs(skeleton.Joints[JointType.HandLeft].Position.Z - skeleton.Joints[JointType.HandRight].Position.Z) < 0.1
                )
            {
                return GesturePartResult.Succeeded;    
            }
            return GesturePartResult.Failed;
        }
    }

    /// <summary>
    /// Vlase SlideSegmentR1
    /// Representa la posición de ambos brazos a la derecha del hombro derecho.
    /// </summary>
    public class SlideSegmentR1 : IGestureSegment
    {
  
        public GesturePartResult Update(Skeleton skeleton)
        {
            if (skeleton.Joints[JointType.HandRight].Position.X > skeleton.Joints[JointType.ShoulderRight].Position.X &&
                skeleton.Joints[JointType.HandLeft].Position.X > skeleton.Joints[JointType.ShoulderRight].Position.X
                )
            {
                return GesturePartResult.Succeeded;
            }
            return GesturePartResult.Failed;
        }
    }

    /// <summary>
    /// Vlase SlideSegmentR1
    /// Representa la posición de ambos brazos a la izquierda del hombro izquierdo.
    /// </summary>
    public class SlideSegmentR2 : IGestureSegment
    {
        public GesturePartResult Update(Skeleton skeleton)
        {
            if (skeleton.Joints[JointType.HandRight].Position.X < skeleton.Joints[JointType.ShoulderLeft].Position.X &&
                skeleton.Joints[JointType.HandLeft].Position.X < skeleton.Joints[JointType.ShoulderLeft].Position.X                
                )
            {
                return GesturePartResult.Succeeded;
            }
            return GesturePartResult.Failed;
        }
    }
}
