# Precision Dark Design System

## Brand & Style

The design system is a high-performance, developer-centric interface that prioritizes technical clarity and focus. It employs a **Modern Minimalist** style with a heavy emphasis on **Dark Mode** aesthetics. The personality is precise, efficient, and sophisticated, targeting a technical audience that values density and low visual noise. 

The UI utilizes a deep, monochromatic foundation to reduce eye strain, while the new **"Electric Cyan"** serves as a high-visibility accent for critical actions and data visualization. Visual interest is achieved through subtle textural differences and sharp, intentional highlights rather than decorative elements.

## Colors

The palette is anchored by a deep obsidian surface (`#051424`) to create a boundless sense of depth. 

- **Primary (Electric Cyan):** Used for primary actions, active navigation states, and focus indicators. It provides a sharp, energetic contrast against the dark background.
- **Surface & Backgrounds:** The base is `#051424`. Elevate components using subtle shifts to `#122131` or `#273647` for containers.
- **Accents & Gradients:** Interactive highlights should use a subtle glow effect or linear gradient from `#0ea5e9` to `#38bdf8` at a 45-degree angle.
- **Status:** Use the primary cyan for "Success/Active" logic, maintaining a cohesive technical aesthetic across all states.

### Color Palette

```
Background: #051424
Surface: #051424
Surface Variant: #273647
Primary: #89ceff (Electric Cyan)
Primary Container: #0ea5e9
Secondary: #bcc7de
Tertiary: #ffb86e
Error: #ffb4ab
```

## Typography

This design system uses **Geist** for all primary interface text, leveraging its tight apertures and geometric precision. For technical data, code snippets, or status labels, **JetBrains Mono** is introduced to provide a distinct "utility" feel.

### Font Scale

```
Display LG: 48px, 700 weight, -0.02em letter-spacing
Headline LG: 32px, 600 weight, -0.01em letter-spacing
Headline LG Mobile: 24px, 600 weight
Body MD: 16px, 400 weight, 24px line-height
Body SM: 14px, 400 weight, 20px line-height
Label MD: 12px, 500 weight, 0.05em letter-spacing, JetBrains Mono
```

- **Tracking:** Headlines should feature slightly negative letter spacing to feel "locked-in" and dense.
- **Weight:** Use SemiBold (600) for hierarchy in headlines and Medium (500) for interactive labels.
- **Contrast:** High-level information uses White (`#FFFFFF`), while secondary body text uses the neutral slate (`#94a3b8`) to maintain visual hierarchy.

## Layout & Spacing

The layout follows a **Fixed Grid** model on desktop and a **Fluid** model on mobile. A 12-column system is used for desktop layouts, while a 4-column system is used for mobile.

### Spacing System

```
Baseline Unit: 4px
Components: 8px (2x), 16px (4x), or 24px (6x) for internal padding
Gutter: 16px
Margin Desktop: 32px
Margin Mobile: 16px
Max Width: 1440px
```

- **Rhythm:** All spacing is derived from a 4px baseline. Components should generally use 8px (2x), 16px (4x), or 24px (6x) for internal padding.
- **Density:** This design system leans toward a high-density layout. Vertical rhythm should be tight, using small margins between related technical data points.
- **Safe Areas:** Ensure a 32px margin on desktop to allow the dark interface to feel expansive rather than cramped.

## Elevation & Depth

Depth is conveyed through **Tonal Layers** and **Low-Contrast Outlines** rather than traditional shadows.

### Layer System

```
Level 0 (Base): #051424
Level 1 (Surface): #122131 
Level 2 (Containers): #273647
```

- **Layering:** Surfaces \"rise\" by becoming lighter. Level 0 is `#051424`, Level 1 is `#122131`, and Level 2 (modals/popovers) is `#273647`.
- **Borders:** Use 1px borders with low opacity (`rgba(255, 255, 255, 0.1)`) to define element boundaries.
- **Active State:** For active cards or focused inputs, the border should transition to the Primary Electric Cyan (`#0ea5e9`) with a subtle 4px outer glow of the same color at 20% opacity.

## Shapes

The shape language is **"Soft"** yet disciplined. While the interface feels technical, subtle rounding prevents it from feeling aggressive or dated.

### Border Radius

```
Standard Elements: 0.25rem (4px) - Buttons, inputs, tags
Large Containers: 0.75rem (12px) - Cards and modals
Full: 9999px - Pills/badges
```

- **Standard Elements:** Buttons, inputs, and tags use a 0.25rem (4px) radius.
- **Containers:** Large cards and modals use a 0.75rem (12px) radius to create a distinct container hierarchy.
- **Interactions:** Hover states should maintain the same corner radius but may introduce a slight 1px inset border to emphasize the shape.

## Components

### Buttons
- **Primary:** Solid Electric Cyan (`#0ea5e9`) with black text for maximum legibility.
- **Secondary:** Outlined with cyan text.
- **Ghost:** No background, cyan text only.

### Chips/Tags
Use JetBrains Mono for the text. Backgrounds should be a dark tint of cyan (e.g., `#0ea5e9` at 10% opacity) with a solid cyan left-border marker.

### Input Fields
Default state is a dark fill (`#171717`) with a 1px slate border. Focus state triggers a 1px Electric Cyan border and a subtle cyan text cursor.

### Lists
Use subtle dividers (`#ffffff` at 5% opacity). Hovering over a list item should trigger a slight background shift to `#1e293b`.

### Cards
Flat design with a 1px border. No shadows. Use the primary cyan for any iconography or "read more" links within the card.

### Interactive States
All hover, active, and focus transitions should be snappy (150ms) to reinforce the "precision" feel of the system.

## CSS Variables

The design system is implemented through comprehensive CSS variables for consistency:

```css
--background: #051424;
--surface: #051424;
--primary: #89ceff;
--primary-container: #0ea5e9;
--secondary: #bcc7de;
--tertiary: #ffb86e;
--on-surface: #d4e4fa;
--on-primary: #00344d;
--outline: #88929b;
--surface-container: #122131;
--surface-variant: #273647;
```

All component styles should reference these CSS variables to ensure consistency across the application.