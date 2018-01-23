using Microsoft.Kinect;
using System;

/// <summary>
/// Fichero CompleteGesture.cs
/// Contiene un conjunto de clases que representan una secuencia completa de movimientos que definen un gesto.
/// </summary>
namespace KinectSimpleGesture
{
    /// <summary>
    /// Clase WaveGestureR
    /// Representa un movimiento de deslizamiento del brazo de derecho de derecha a izquierda.
    /// </summary>
    public class WaveGestureR
    {
        readonly int WINDOW_SIZE = 500; // Tiempo de detección máximo (en frames)

        IGestureSegment[] _segments; // Segmentos que componen en gesto.

        int _currentSegment = 0; // Contadores de frames y de segmento.
        int _frameCount = 0;

        public event EventHandler GestureRecognized; // Manejador cuando se detecta el gesto.

        public WaveGestureR()
        {
            WaveSegmentR1 waveRightSegment1 = new WaveSegmentR1();
            WaveSegmentR2 waveRightSegment2 = new WaveSegmentR2();

            _segments = new IGestureSegment[]
            {
                waveRightSegment1, // Segmento brazo a la derecha.
                waveRightSegment2, // Segmento brazo a la izquierda.
             
            };
        }

        /// <summary>
        /// Actualización del gesto.
        /// </summary>
        /// <param name="skeleton">Datos del skeleton</param>
        public void Update(Skeleton skeleton)
        {            
            GesturePartResult result = _segments[_currentSegment].Update(skeleton);

            if (result == GesturePartResult.Succeeded)
            {
                
                if (_currentSegment + 1 < _segments.Length) // Detección de segmento.
                {
                    _currentSegment++;
                    _frameCount = 0;
                }
                else
                {
                    if (GestureRecognized != null)
                    {
                        GestureRecognized(this, new EventArgs()); // Gesto completado.
                        Reset();
                    }
                }
            }
            else if (result == GesturePartResult.Failed && _frameCount == WINDOW_SIZE) // Gesto fallido y tiempo agotado.
            {
                Reset();
            }
            else // Gesto fallido aún con tiempo.
            {
                _frameCount++;
            }
            
        }

        /// <summary>
        /// Reinicio del gesto.
        /// </summary>
        public void Reset()
        {
            _currentSegment = 0;
            _frameCount = 0;
        }
    }

    /// <summary>
    /// Clase WaveGestureL
    /// Representa un movimiento de deslizamiento del brazo izquierdo de izquierda a derecha.
    /// </summary>
    public class WaveGestureL
    {
        readonly int WINDOW_SIZE = 500;

        IGestureSegment[] _segments;

        int _currentSegment = 0;
        int _frameCount = 0;

        public event EventHandler GestureRecognized;

        public WaveGestureL()
        {
            WaveSegmentL1 waveRightSegmentL1 = new WaveSegmentL1();
            WaveSegmentL2 waveRightSegmentL2 = new WaveSegmentL2();

            _segments = new IGestureSegment[]
            {
                waveRightSegmentL1,
                waveRightSegmentL2,

            };
        }

        public void Update(Skeleton skeleton)
        {

            GesturePartResult result = _segments[_currentSegment].Update(skeleton);

            if (result == GesturePartResult.Succeeded)
            {

                if (_currentSegment + 1 < _segments.Length)
                {
                    //Console.WriteLine("S"+_currentSegment);
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

    /// <summary>
    /// Clase ClapGestureIn
    /// Representa un gesto que culmina produciendo una palmada.
    /// </summary>
    public class ClapGestureIn
    {
        readonly int WINDOW_SIZE = 500;

        IGestureSegment[] _segments;

        int _currentSegment = 0;
        int _frameCount = 0;

        public event EventHandler GestureRecognized;

        public ClapGestureIn()
        {
            ClapSegment1 clapSegment1 = new ClapSegment1(); // Segmento palmas alejadas.
            ClapSegment2 clapSegment2 = new ClapSegment2(); // Segmento palmas juntas.

            _segments = new IGestureSegment[]
            {
                clapSegment1,
                clapSegment2,

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

    /// <summary>
    /// Clase ClapGestureOut
    /// Representa un movimiento de separación de manos, partiendo de estas estando juntas.
    /// </summary>
    public class ClapGestureOut
    {
        readonly int WINDOW_SIZE = 500;

        IGestureSegment[] _segments;

        int _currentSegment = 0;
        int _frameCount = 0;

        public event EventHandler GestureRecognized;

        public ClapGestureOut()
        {
            ClapSegment1 clapSegment1 = new ClapSegment1();
            ClapSegment2 clapSegment2 = new ClapSegment2();

            _segments = new IGestureSegment[]
            {
                clapSegment2, // Segmento palmas juntas.
                clapSegment1, // Segmento palmas alejadas.

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

    /// <summary>
    /// Clase SlideGestureR
    /// Representa un movimiento fuerte de deslizamiento de ambos brazos de derecha a izquierda.
    /// </summary>
    public class SlideGestureR
    {
        readonly int WINDOW_SIZE = 500;

        IGestureSegment[] _segments;

        int _currentSegment = 0;
        int _frameCount = 0;

        public event EventHandler GestureRecognized;

        public SlideGestureR()
        {
            SlideSegmentR1 slideSegment1 = new SlideSegmentR1(); //Segmento brazos a derecha.
            SlideSegmentR2 slideSegment2 = new SlideSegmentR2(); //Segemento brazos a izquierda.

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


