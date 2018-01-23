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
            if (skeleton.Joints[JointType.HandRight].Position.Y > skeleton.Joints[JointType.ElbowRight].Position.Y)
            {
                // Mano a la derecha del hombro.
                if (skeleton.Joints[JointType.HandRight].Position.X > skeleton.Joints[JointType.ElbowRight].Position.X + 0.1)
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
            if (skeleton.Joints[JointType.HandRight].Position.Y > skeleton.Joints[JointType.ElbowRight].Position.Y)
            {
                // Mano a la izquierda del hombro
                if (skeleton.Joints[JointType.HandRight].Position.X < skeleton.Joints[JointType.ElbowRight].Position.X - 0.12)
                {
                    return GesturePartResult.Succeeded;
                }
            }

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
            if (skeleton.Joints[JointType.HandLeft].Position.Y > skeleton.Joints[JointType.ElbowLeft].Position.Y)
            {
                // Mano por la izquierda del hombro
                Console.WriteLine(skeleton.Joints[JointType.HandLeft].Position.X);
                Console.WriteLine(skeleton.Joints[JointType.ElbowLeft].Position.X);
                if (skeleton.Joints[JointType.HandLeft].Position.X < skeleton.Joints[JointType.ElbowLeft].Position.X - 0.1)
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
            if (skeleton.Joints[JointType.HandLeft].Position.Y > skeleton.Joints[JointType.ElbowLeft].Position.Y)
            {
                // Mano a la derecha del hombro
                if (skeleton.Joints[JointType.HandLeft].Position.X > skeleton.Joints[JointType.ElbowLeft].Position.X + 0.12)
                {
                    return GesturePartResult.Succeeded;
                }
            }

            return GesturePartResult.Failed;
        }
    }


    public class ClapSegment1 : IGestureSegment
    {
        public GesturePartResult Update(Skeleton skeleton)
        {
            // NO IMPLEMENTADO EN ESTA APLICACIÓN. IMPLEMENTADO SOLO EN KINECTAPP2
            return GesturePartResult.Failed;
        }
    }

    public class ClapSegment2 : IGestureSegment
    {

        public GesturePartResult Update(Skeleton skeleton)
        {
            // NO IMPLEMENTADO EN ESTA APLICACIÓN. IMPLEMENTADO SOLO EN KINECTAPP2
            return GesturePartResult.Failed;
        }
    }

    /// <summary>
    /// Clase SilenceSegment1
    /// Representa la posición de muñeca derecha alejada del eje de los hombros previa al gesto de silencio.
    /// </summary>
    public class SilenceSegment1 : IGestureSegment
    {
        public GesturePartResult Update(Skeleton skeleton)
        {
            // Distancia entre muñeca derecha y eje de hombros superior en cada eje a un umbral
            if (Math.Abs(skeleton.Joints[JointType.WristRight].Position.X - skeleton.Joints[JointType.ShoulderCenter].Position.X) >= 0.1 &&
                Math.Abs(skeleton.Joints[JointType.WristRight].Position.Y - skeleton.Joints[JointType.ShoulderCenter].Position.Y) >= 0.1 &&
                Math.Abs(skeleton.Joints[JointType.WristRight].Position.Z - skeleton.Joints[JointType.ShoulderCenter].Position.Z) >= 0.35)
            {
                return GesturePartResult.Succeeded;
            }
            return GesturePartResult.Failed;
        }
    }

    /// <summary>
    /// Clase SilenceSegment2
    /// Representa la posición de muñeca derecha cerca del eje de los hombros que concluye con el gesto de silencio.
    /// </summary>
    public class SilenceSegment2 : IGestureSegment
    {
        public GesturePartResult Update(Skeleton skeleton)
        {
            if (Math.Abs(skeleton.Joints[JointType.WristRight].Position.X - skeleton.Joints[JointType.ShoulderCenter].Position.X) < 0.1 &&
                Math.Abs(skeleton.Joints[JointType.WristRight].Position.Y - skeleton.Joints[JointType.ShoulderCenter].Position.Y) < 0.1 &&
                Math.Abs(skeleton.Joints[JointType.WristRight].Position.Z - skeleton.Joints[JointType.ShoulderCenter].Position.Z) < 0.35)

            {
                return GesturePartResult.Succeeded;
            }
            return GesturePartResult.Failed;
        }
    }

    // Gestos no utilizados finalmente en esta aplicación
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

    public class SlideGestureR
    {
        readonly int WINDOW_SIZE = 500;

        IGestureSegment[] _segments;

        int _currentSegment = 0;
        int _frameCount = 0;

        public event EventHandler GestureRecognized;

        public SlideGestureR()
        {
            SlideSegmentR1 slideSegment1 = new SlideSegmentR1();
            SlideSegmentR2 slideSegment2 = new SlideSegmentR2();

            _segments = new IGestureSegment[]
            {
                slideSegment1,
                slideSegment2,

            };
        }

        
        public void Update(Skeleton skeleton)
        {

            GesturePartResult result = _segments[_currentSegment].Update(skeleton);

            if (result == GesturePartResult.Succeeded)
            {

                if (_currentSegment + 1 < _segments.Length)
                {
                    _currentSegment++;
                    _frameCount = 0;
                }
                else
                {
                    if (GestureRecognized != null)
                    {
                        GestureRecognized(this, new EventArgs());
                        Reset();
                    }
                }
            }
            else if (result == GesturePartResult.Failed && _frameCount == WINDOW_SIZE)
            {
                Reset();
            }
            else
            {
                _frameCount++;
            }

        }

        public void Reset()
        {
            _currentSegment = 0;
            _frameCount = 0;
        }
    }
}


