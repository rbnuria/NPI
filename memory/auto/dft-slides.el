(TeX-add-style-hook
 "dft-slides"
 (lambda ()
   (TeX-add-to-alist 'LaTeX-provided-class-options
                     '(("beamer" "11pt" "compress")))
   (add-to-list 'LaTeX-verbatim-environments-local "semiverbatim")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "path")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "url")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "nolinkurl")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "hyperbaseurl")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "hyperimage")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "hyperref")
   (add-to-list 'LaTeX-verbatim-macros-with-delims-local "path")
   (TeX-run-style-hooks
    "latex2e"
    "beamer"
    "beamer11"
    "spanish"
    "slides"
    "booktabs"
    "comment")
   (TeX-add-symbols
    "diff"
    "C"
    "R"
    "Q"
    "Z"
    "N"
    "doctitle"
    "docauthor"
    "docaddress"
    "docemail")
   (LaTeX-add-xcolor-definecolors
    "TurkishRose"
    "ChetwodeBlue"))
 :latex)

