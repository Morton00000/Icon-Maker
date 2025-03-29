# Icon Maker
This was make so that I could color and unstitch the icons sheets for the Monster Hunter Wiki easyer.

## How To
The program is expecting an image in .png format and a text file with extension .config of the same name.

There can be multiple pairs of these files.

It is expecting them to be in the same folder as the .jar file.

The .png image is expected to be a 16 x 16 set of images.

The resolution of the image does not matter as long as it is divisible by 16.

The file has no UI and does not show any progress bar when running.

## Config
The config file has several setup options.

The first line should be "output=/".

You can add a folder path to this but it must end in "/".

## Colors
Each color should be placed on its own line with the color hex (starting with "#") the = and the color name as it should appear in the icon names.

Example: 

#DE515A=Red

## Path and Names
Next are the icon locations.

This is done with the row then collom seperate with a "." followed by an "=" and then the filename without an extension.

The file names can have spaces.

Example:

3.5=Testing


If the icon needs to be colored add "<#>" to the name part of the entry.

This will be replaced with the color name setup at the top and the image will be color based on the code give.

The color is applyed with a multiply.

Any lines without the color tag "<#>" will still be unstitched but not colored.

Example:

3.5=Red Icon <#>


You can comment our lines by starting them with a "*".

Example:

*3.5=Testing

## Output Notes
The config name can also be a filepath then filename to have images go to different locations.

This filepath will be added to the master output path noted on the first line.

Example:

5.6=icons/Test

If the unsticher finds a file with the same name that it is trying to create it will check if the files are the same.

If the files are the same it will do nothing, if they are different then it will override the file.
