   M S B A M L   `   `   `  ����M  IControlsBasics-WPF, Version=0.0.0.0, Culture=neutral, PublicKeyToken=nulla5clr-namespace:Microsoft.Samples.Kinect.ControlsBasics'Microsoft.Samples.Kinect.ControlsBasics  5   	   8    2Microsoft.Samples.Kinect.ControlsBasics.MainWindow   5   	   W SPresentationCore, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35R NWindowsBase, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35\ XPresentationFramework, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35D 9http://schemas.microsoft.com/winfx/2006/xaml/presentation    5   	   R NSystem.Xaml, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089:x,http://schemas.microsoft.com/winfx/2006/xaml     5   	   \ XMicrosoft.Kinect.Toolkit.Controls, Version=0.0.0.0, Culture=neutral, PublicKeyToken=nullS OMicrosoft.Kinect.Toolkit, Version=1.8.0.0, Culture=neutral, PublicKeyToken=null2k(http://schemas.microsoft.com/kinect/2013   5   	   <d2http://schemas.microsoft.com/expression/blend/2008   5   	   Fmc;http://schemas.openxmlformats.org/markup-compatibility/2006   5   	   Clocal5clr-namespace:Microsoft.Samples.Kinect.ControlsBasics   5   	   -   5        $��ControlsBasicsWindow��5   	     � WindowState$  	Maximized=�5   F    � Title$ Controls Basics��5	   	   $	��500��5   	   $	��700��5   	   $��Segoe UI)�5   	    � 	Resources 5         ChevronGeometry  	ChevronUp �� Stretch
 �� Top �� FallbackValue �� Path �� RelativeSource �� AncestorType?  9Microsoft.Samples.Kinect.ControlsBasics.KinectHoverButton	 J� RenderTransformOrigin
 �� Angle  ChevronRight  ChevronDown  ChevronLeft  KinectHoverButtonStyle �� 
TargetType �� Property �� Value �� 
TargetType �� Triggers P� Property P� Value " KinectHoverButtonScrollUpStyle �� BasedOn % !KinectHoverButtonScrollRightStyle $  KinectHoverButtonScrollDownStyle $	  KinectHoverButtonScrollLeftStyle��%�  &	       &	 �    7   &	 Y   7   &	 .   7   &	    7   &	 �    &	 �    7  7  &	 ?    7  7  &	 �    7  7  &		 �    7  7  B�5   
   miF1M150.001,146.9932L126.565,169.3822L126.565,153.5962L150.001,130.6182L173.435,153.5962L173.435,169.3822z�5      F� 5   
   $��48��6!   $��48��6-   $ Uniform=�68   $ 0��6J   8��  ��5      �� $ Black��$ 
Foreground� �� "  $	 0.5,0.5&�5   M   z�5      �� 5      $
 0Q�6"   5      F� 5!   
   $��48��6$   $��48��60   $ Uniform=�6;   $ 0��6M   8��  ��5#      �� $ Black��$ 
Foreground� �� "  $	 0.5,0.5&�5$   M   z�5%      �� 5&      $
 90Q�6"   5(      F� 5*   
   $��48��6#   $��48��6/   $ Uniform=�6:   $ 0��6L   8��  ��5,      �� $ Black��$ 
Foreground� �� "  $	 0.5,0.5&�5-   M   z�5.      �� 5/      $	
 180Q�6"   51      F� 53   
   $��48��6#   $��48��6/   $ Uniform=�6:   $ 0��6L   8��  ��55      �� $ Black��$ 
Foreground� �� "  $	 0.5,0.5&�56   M   z�57      �� 58      $	
 270Q�6"   5:      �� 5<   
   "  6/   .�5=      ��  � ��
 �����6+   �� 5>       � ��
 �����6,   �� 5?       � ��$ 0q�60   6<   �� 5@       � ��
 �����6;   �� 5A       � �� 5B      �� 5C      "  6&   .R�5D      � $��Stretch=�6   $��Stretch=�6=   #��z��6Y   #��z��6�   #��z��6�   .+�5E      �� #��z��6%   #��z��6Y   ._�5F   "   �� $��Stretch=�63   $��Stretch=�6Q   #��z��6m   6�   5G      5H      5I      5K       5L      P� 5M       � {� .  . �5N      ��  � ��
 �����63   �� 5O       � ��
 �����64   �� 5P       � ��
 ���� 6I   5Q      P� 5R       �@ IsHandPointerOver . . �5S      ��  � �� �#5A5B5B63   �� 5T       � ��
 �����63   5U      5W      �� 5Y   
   "  67   8   .�5Z      ��  � ��8  6(   5[      �� 5]   
   "  6:   8   .�5^      ��  � ��8  6(   5_      �� 5a   
   "  69   8   .�5b      ��  � ��8  6(   5c      �� 5e   
   "  69   8   .�5f      ��  � ��8  6(   5g      5h      .��5j      � $	 0.503,0.518&�6    � RowDefinitions 5k   
   �� 5l      $
��Auto �6   �� 5m      $��* �6   .+�5p   
   � $��0��6   $��10,50,10,523q�6   $��2��63   .+�5r      8  2Microsoft.Kinect.Toolkit.Controls.KinectUserViewer  $	��100��6b   $��Center=�6o   $	��Top=�6�   4  .Microsoft.Kinect.Toolkit.Controls.KinectRegion   KinectRegion 6!   ��  �� ElementName$ kinectRegion��6�   4  .Microsoft.Kinect.Toolkit.KinectSensorChooserUI  5s      -   5        $��sensorChooserUi��5s   [   $��Center=�6&   $	��Top=�6C   6[   5t        5v   
   -   5          Name$ kinectRegion��5v   &   $��0��6   $	 0.495,0.311&�6<   .��5w      � -   5         �Name$ kinectRegionGrid��5w      $��10,-18,10,20q�6-    5x      �� 5y      $	��300 �6$   �� 5z      $��80 �6$   �� 5{      $	��300 �6$   .+�5~      � $��1��6   $��Stretch=�6$   $��Stretch=�6B   $��0, 10, 0, 10q�6^    � ColumnDefinitions 5      �� 5�      $��* �6+   �� 5�      $��10 �6+   �� 5�      $��* �6+   5�      � 5�      -   5        $
 lang��5�      :  4Microsoft.Kinect.Toolkit.Controls.KinectScrollViewer  5�      -   5        $��scrollViewer��5�   4   $��0��6'   $��Visible=�6H   $��Disabled=�6p   $��-10,25,10,0q�6�   .��5�      � -   5         �Name$ 	wrapPanel��5�   ;   $��Bottom=�6     � Orientation$ 
Horizontal=�6N   .+�5�      8  2Microsoft.Kinect.Toolkit.Controls.KinectTileButton  -   5          Name$
 btGB��5�   -   $��KinectTileButton��6;   $��	10,43,0,0q�6V      Label$ Hello��6�   $	��150��6�   $	��180��6�   $��2��6�   ��5�      �� 5�   "   .R�5�   &   �� $��Images/BanderaGB.png��6,   6L   5�   #   5�        5�      -	   5        $ btEsp��5�   -   $��KinectTileButton��6<   $��	10,43,0,0q�6W   $
 Hola��6�   $	��150��6�   $	��180��6�   $	 
0.49,1.442&�6�   ��5�      �� 5�   "   .R�5�   &   �� $��Images/BanderaEsp.png��6,   5�   #   5�        5�      -
   5        $ btIta��5�   -   $��KinectTileButton��6<   $��	10,43,0,0q�6W   $
 Ciao��6�   $	��150��6�   $	��180��6�   $	 
0.49,1.442&�6�   ��5�      �� 5�   "   .R�5�   &   �� $��Images/BanderaIta.jpg��6,   5�   #   5�        5�      -   5        $ btFra��5�   -   $��KinectTileButton��6<   $��	10,43,0,0q�6W   $ Salut!��6�   $	��150��6�   $	��180��6�   $	 
0.49,1.442&�6�   ��5�      �� 5�   "   .R�5�   &   �� $��Images/BanderaFra.jpg��6,   5�   #   5�        5�      -   5        $ btChi��5�   -   $��KinectTileButton��6<   $��	10,43,0,0q�6W   $ 你好��6�   $	��150��6�   $	��180��6�   $	 
0.49,1.442&�6�   ��5�      �� 5�   "   .R�5�   &   �� $��Images/BanderaChi.jpg��6,   5�   #   5�        5�      -   5        $ btJap��5�   -   $��KinectTileButton��6<   $��	10,43,0,0q�6W   $ 	ハロー��6�   $	��150��6�   $	��180��6�   $	 
0.49,1.442&�6�   ��5�      �� 5�   "   .R�5�   &   �� $��Images/BanderaJap.jpg��6,   5�   #   5�        5�      -   5        $ btRus��5�   -   $��KinectTileButton��6<   $��	10,43,0,0q�6W   $ привет��6�   $	��150��6�   $	��180��6�   $	 
0.49,1.442&�6�   ��5�      �� 5�   "   .R�5�   &   �� $��Images/BanderaRus.jpg��6,   5�   #   5�        5�      -   5        $ btPor��5�   -   $��KinectTileButton��6<   $��	10,43,0,0q�6W   $
 Olá��6�   $	��150��6�   $	��180��6�   $	 
0.49,1.442&�6�   ��5�      �� 5�   "   .R�5�   &   �� $��Images/BanderaPor.jpg��6,   5�   #   5�      5�      5�      � 5�      $��1��6   $��Stretch=�6$   $��Stretch=�6B   $��0, 10, 0, 10q�6^    5�      �� 5�      $	��250 �6+   �� 5�      $	��600 �6+   �� 5�      $	��250 �6+   .+�5�        ?   9Microsoft.Samples.Kinect.ControlsBasics.KinectHoverButton   Click$ PageLeftButtonClick��6x   $��0��6.   #��[	 6>   $��-6,0,7,0q�6�   {�6�   �� $ ControlsBasicsWindow��$ PageLeftEnabled�6�     5�      $ PageRightButtonClick��6y   $��2��6.   #��[ 6>   $��
75,0,-59,0q�6�   {�6�   �� $ ControlsBasicsWindow��$ PageRightEnabled�6�   5�      � 5�      $��2��6   .+�5�      �� -   5         ��Name$ PersonajeImg��5�      $��Images/tragabuche.jpg��62   5�      5�      5�      5�      5�      