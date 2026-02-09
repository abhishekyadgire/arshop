# 3D Models Directory

This directory contains 3D model files for AR product visualization.

## Overview

Place your product 3D models in this directory. The models will be loaded by the AR module to display products in augmented reality.

## Supported Formats

- **GLB** (GL Transmission Format Binary) - Recommended ✅
- **GLTF** (GL Transmission Format)

GLB is preferred as it's a single binary file containing all resources.

## File Naming Convention

Use the following naming pattern for consistency:

```
product_[product_id].glb
```

Examples:
- `product_12345.glb`
- `product_shoe_001.glb`
- `product_furniture_chair_01.glb`

## Model Requirements

### Size Recommendations
- **File Size**: Keep under 10MB for optimal loading performance
- **Polygon Count**: 5,000 - 50,000 polygons recommended
- **Texture Resolution**: 2048x2048 or lower

### Technical Requirements
- Models should be properly UV unwrapped
- Include materials and textures in the GLB file
- Use PBR (Physically Based Rendering) materials
- Center the model at origin (0, 0, 0)
- Apply appropriate scale (1 unit = 1 meter)

## Model Orientation

- **Shoes**: Facing forward (toe pointing +Z)
- **Furniture**: Front facing +Z
- **Clothing**: Front facing camera
- **Accessories**: Natural orientation

## Adding New Models

1. **Prepare Model**: Export from your 3D software (Blender, Maya, etc.)
2. **Optimize**: Use tools like glTF-Pipeline to compress
3. **Test**: Verify in ARCore Viewer or Scene Viewer
4. **Name**: Follow naming convention
5. **Upload**: Place in this directory
6. **Link**: Reference in Firebase product document

## Example Product Document

```json
{
  "id": "12345",
  "name": "Running Shoes",
  "modelUrl": "models/product_12345.glb",
  "arEnabled": true
}
```

## Optimization Tools

- **Blender**: Built-in glTF exporter
- **glTF-Pipeline**: Command-line optimization tool
- **RapidCompact**: Online 3D optimization service
- **Sketchfab**: 3D model marketplace with optimization

## Testing

Before adding to production:

1. Test on multiple devices
2. Verify scale is correct
3. Check textures load properly
4. Ensure no missing materials
5. Test AR placement and interaction

## Placeholder Models

During development, you can use:
- Simple geometric shapes (cube, sphere, cylinder)
- Free models from Sketchfab (with proper licensing)
- Placeholder GLB files from glTF sample models

## Resources

- [Khronos glTF Overview](https://www.khronos.org/gltf/)
- [glTF Sample Models](https://github.com/KhronosGroup/glTF-Sample-Models)
- [ARCore Best Practices](https://developers.google.com/ar/develop/best-practices)
- [Blender glTF Export](https://docs.blender.org/manual/en/latest/addons/import_export/scene_gltf2.html)

## Notes

⚠️ **Do NOT commit large model files to Git**
- Add `*.glb` and `*.gltf` to `.gitignore` if models are large
- Use Firebase Storage or CDN for production models
- Keep only small placeholder models in repository

📝 **Model Attribution**
- Track source and license for each model
- Maintain a separate `MODELS.md` for attribution if using third-party models
