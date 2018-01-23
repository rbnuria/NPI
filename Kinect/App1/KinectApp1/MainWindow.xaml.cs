//------------------------------------------------------------------------------
// Fichero adaptado del ejemplo ControlBasics
//------------------------------------------------------------------------------

namespace Microsoft.Samples.Kinect.ControlsBasics
{
    using System;
    using System.Globalization;
    using System.Linq;
    using System.Windows;
    using System.Windows.Controls;
    using System.Windows.Data;
    using System.Windows.Media;
    using System.Windows.Media.Imaging;
    using KinectSimpleGesture;
    using Microsoft.Kinect;
    using Microsoft.Kinect.Toolkit;
    using Microsoft.Kinect.Toolkit.Controls;
    using Microsoft.Kinect.Toolkit.Interaction;

    /// <summary>
    /// Interaction logic for MainWindow
    /// </summary>
    public partial class MainWindow
    {
        public static readonly DependencyProperty PageLeftEnabledProperty = DependencyProperty.Register(
            "PageLeftEnabled", typeof(bool), typeof(MainWindow), new PropertyMetadata(false));

        public static readonly DependencyProperty PageRightEnabledProperty = DependencyProperty.Register(
            "PageRightEnabled", typeof(bool), typeof(MainWindow), new PropertyMetadata(false));

        private const double ScrollErrorMargin = 0.001;

        private const int PixelScrollByAmount = 10;

        private KinectSensorChooser sensorChooser;
        private KinectSensor sensor;

        // Declaración de gestos a detectar.
        WaveGestureR _gestureR = new WaveGestureR();
        WaveGestureL _gestureL = new WaveGestureL();
        ClapGestureIn _gestureClapIn = new ClapGestureIn();
        ClapGestureOut _gestureClapOut = new ClapGestureOut();
        SilenceGestureIn _gestureSilenceIn = new SilenceGestureIn();
        SlideGestureR _gestureSlideR = new SlideGestureR();

        // Imágenes de personajes.
        private readonly static string[] pers_images = new string[] { "Images/tragabuche.jpg", "Images/asterix.jpg", "Images/jasmin.jpg", "Images/pedro.jpg" };
        private int current_image_index = 0;
        private bool isPlayerRunning = false;

        //Para el cambio de ventanas (no implementado).
        private Window switchwindow;

        // Reproductor de música.
        System.Media.SoundPlayer player;

        /// <summary>
        /// Initializes a new instance of the <see cref="MainWindow"/> class. 
        /// </summary>
        public MainWindow()
        {
            this.InitializeComponent();

            // initialize the sensor chooser and UI
            this.sensorChooser = new KinectSensorChooser();
            this.sensorChooser.KinectChanged += SensorChooserOnKinectChanged;
            this.sensorChooserUi.KinectSensorChooser = this.sensorChooser;
            this.sensorChooser.Start();

            // Bind the sensor chooser's current sensor to the KinectRegion
            var regionSensorBinding = new Binding("Kinect") { Source = this.sensorChooser };
            BindingOperations.SetBinding(this.kinectRegion, KinectRegion.KinectSensorProperty, regionSensorBinding);

            // Clear out placeholder content
            //this.wrapPanel.Children.Clear();

            // Add in display content
            for (var index = 0; index < 300; ++index)
            {
                var button = new KinectTileButton { Label = (index + 1).ToString(CultureInfo.CurrentCulture) };
                //   this.wrapPanel.Children.Add(button);
            }

            // Bind listner to scrollviwer scroll position change, and check scroll viewer position
            this.UpdatePagingButtonState();
            scrollViewer.ScrollChanged += (o, e) => this.UpdatePagingButtonState();

            // Inicialización de la detección de gestos.
            sensor = KinectSensor.KinectSensors.Where(
                        s => s.Status == KinectStatus.Connected).FirstOrDefault();

            if (sensor != null)
            {
                sensor.SkeletonStream.Enable();
                sensor.SkeletonFrameReady += Sensor_SkeletonFrameReady;

                _gestureR.GestureRecognized += Gesture_GestureRecognizedR;
                _gestureL.GestureRecognized += Gesture_GestureRecognizedL;
                //_gestureClapIn.GestureRecognized += Gesture_GestureRecognizedClapIn;
                //_gestureClapOut.GestureRecognized += Gesture_GestureRecognizedClapOut;
                _gestureSilenceIn.GestureRecognized += Gesture_GestureRecognizedSilenceIn;
                _gestureSlideR.GestureRecognized += Gesture_GestureRecognizedSlideR;

                sensor.Start();

            }

            // Reproductor de música.
            player = new System.Media.SoundPlayer();
            player.SoundLocation = "Media/vivaldi.wav";
            player.Play();
            isPlayerRunning = true;
            
        }

        /// <summary>
        /// Evento para activar la detección de gestos tras recibir el skeleton.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        void Sensor_SkeletonFrameReady(object sender, SkeletonFrameReadyEventArgs e)
        {
            using (var frame = e.OpenSkeletonFrame())
            {
                if (frame != null)
                {
                    Skeleton[] skeletons = new Skeleton[frame.SkeletonArrayLength];

                    frame.CopySkeletonDataTo(skeletons);

                    if (skeletons.Length > 0)
                    {
                        var user = skeletons.Where(
                                   u => u.TrackingState == SkeletonTrackingState.Tracked).FirstOrDefault();

                        if (user != null)
                        {
                            _gestureR.Update(user);
                            _gestureL.Update(user);
                            _gestureClapIn.Update(user);
                            //_gestureClapOut.Update(user);
                            _gestureSilenceIn.Update(user);
                            _gestureSlideR.Update(user);
                        }
                    }
                }
            }
        }

        /* FUNCIONES DE MANEJO DE GESTOS */
        // Gestionan los distintos eventos asociados a los gestos detectados. Implementan el evento GestureRecognized de las clases en CompleteGesture.cs.

        //Brazo derecho de derecha a izquierda. Se cambia el personaje.
        void Gesture_GestureRecognizedR(object sender, EventArgs e)
        {
            current_image_index = (current_image_index + 1) % pers_images.Length;
            string current_image = pers_images[current_image_index];
            Image myImage = this.PersonajeImg;
            BitmapImage bi = new BitmapImage();
            bi.BeginInit();
            bi.UriSource = new Uri(current_image, UriKind.Relative);
            bi.EndInit();
            //myImage.Stretch = Stretch.Fill;
            myImage.Source = bi;
        }

        //Brazo izquierdo de izquierda a derecha. Se cambia la imagen de fondo.
        void Gesture_GestureRecognizedL(object sender, EventArgs e)
        {
            current_image_index = ((current_image_index - 1) % pers_images.Length + pers_images.Length) % pers_images.Length;
            string current_image = pers_images[current_image_index];
            Image myImage = this.PersonajeImg;
            BitmapImage bi = new BitmapImage();
            bi.BeginInit();
            bi.UriSource = new Uri(current_image, UriKind.Relative);
            bi.EndInit();
            //myImage.Stretch = Stretch.Fill;
            myImage.Source = bi;
        }
         
        // Gesto de silencio. Se cambia el estado del reproductor.
        void Gesture_GestureRecognizedSilenceIn(object sender, EventArgs e)
        {
            if (isPlayerRunning)
            {
                player.Stop();
                isPlayerRunning = false;
            }
            else
            {
                player.Play();
                isPlayerRunning = true;
            }
        }

        // No implementado.
        void Gesture_GestureRecognizedSlideR(object sender, EventArgs e)
        {
            
        }


        /// <summary>
        /// CLR Property Wrappers for PageLeftEnabledProperty
        /// </summary>
        public bool PageLeftEnabled
        {
            get
            {
                return (bool)GetValue(PageLeftEnabledProperty);
            }

            set
            {
                this.SetValue(PageLeftEnabledProperty, value);
            }
        }

        /// <summary>
        /// CLR Property Wrappers for PageRightEnabledProperty
        /// </summary>
        public bool PageRightEnabled
        {
            get
            {
                return (bool)GetValue(PageRightEnabledProperty);
            }

            set
            {
                this.SetValue(PageRightEnabledProperty, value);
            }
        }

        /// <summary>
        /// Called when the KinectSensorChooser gets a new sensor
        /// </summary>
        /// <param name="sender">sender of the event</param>
        /// <param name="args">event arguments</param>
        private static void SensorChooserOnKinectChanged(object sender, KinectChangedEventArgs args)
        {
            if (args.OldSensor != null)
            {
                try
                {
                    args.OldSensor.DepthStream.Range = DepthRange.Default;
                    args.OldSensor.SkeletonStream.EnableTrackingInNearRange = false;
                    args.OldSensor.DepthStream.Disable();
                    args.OldSensor.SkeletonStream.Disable();
                }
                catch (InvalidOperationException)
                {
                    // KinectSensor might enter an invalid state while enabling/disabling streams or stream features.
                    // E.g.: sensor might be abruptly unplugged.
                }
            }

            if (args.NewSensor != null)
            {
                try
                {
                    args.NewSensor.DepthStream.Enable(DepthImageFormat.Resolution640x480Fps30);
                    args.NewSensor.SkeletonStream.Enable();

                    try
                    {
                        args.NewSensor.DepthStream.Range = DepthRange.Near;
                        args.NewSensor.SkeletonStream.EnableTrackingInNearRange = true;
                    }
                    catch (InvalidOperationException)
                    {
                        // Non Kinect for Windows devices do not support Near mode, so reset back to default mode.
                        args.NewSensor.DepthStream.Range = DepthRange.Default;
                        args.NewSensor.SkeletonStream.EnableTrackingInNearRange = false;
                    }
                }
                catch (InvalidOperationException)
                {
                    // KinectSensor might enter an invalid state while enabling/disabling streams or stream features.
                    // E.g.: sensor might be abruptly unplugged.
                }
            }
        }

        public void setSwitchWindow(Window w)
        {
            this.switchwindow = w; 
        }

        /// <summary>
        /// Execute shutdown tasks
        /// </summary>
        /// <param name="sender">object sending the event</param>
        /// <param name="e">event arguments</param>
        private void WindowClosing(object sender, System.ComponentModel.CancelEventArgs e)
        {
            this.sensorChooser.Stop();
            this.sensor.Stop();
        }

        /// <summary>
        /// Handle a button click from the wrap panel.
        /// </summary>
        /// <param name="sender">Event sender</param>
        /// <param name="e">Event arguments</param>
        private void KinectTileButtonClick(object sender, RoutedEventArgs e)
        {
            var button = (KinectTileButton)e.OriginalSource;

            var selectionDisplay = new SelectionDisplay(button.Label as string);
            this.kinectRegionGrid.Children.Add(selectionDisplay);
            e.Handled = true;
        }

        /// <summary>
        /// Handle paging right (next button).
        /// </summary>
        /// <param name="sender">Event sender</param>
        /// <param name="e">Event arguments</param>
        private void PageRightButtonClick(object sender, RoutedEventArgs e)
        {
            scrollViewer.ScrollToHorizontalOffset(scrollViewer.HorizontalOffset + PixelScrollByAmount);
        }

        /// <summary>
        /// Handle paging left (previous button).
        /// </summary>
        /// <param name="sender">Event sender</param>
        /// <param name="e">Event arguments</param>
        private void PageLeftButtonClick(object sender, RoutedEventArgs e)
        {
            scrollViewer.ScrollToHorizontalOffset(scrollViewer.HorizontalOffset - PixelScrollByAmount);
        }

        /// <summary>
        /// Change button state depending on scroll viewer position
        /// </summary>
        private void UpdatePagingButtonState()
        {
            this.PageLeftEnabled = scrollViewer.HorizontalOffset > ScrollErrorMargin;
            this.PageRightEnabled = scrollViewer.HorizontalOffset < scrollViewer.ScrollableWidth - ScrollErrorMargin;
        }

        ~MainWindow()
        {
            sensor = null;
            sensorChooser = null;
            GC.SuppressFinalize(this);
        }
    }
}
