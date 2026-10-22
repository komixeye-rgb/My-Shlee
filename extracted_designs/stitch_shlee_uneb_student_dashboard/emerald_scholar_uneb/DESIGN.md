---
name: Emerald Scholar UNEB
colors:
  surface: '#f9f9ff'
  surface-dim: '#cfdaf2'
  surface-bright: '#f9f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f0f3ff'
  surface-container: '#e7eeff'
  surface-container-high: '#dee8ff'
  surface-container-highest: '#d8e3fb'
  on-surface: '#111c2d'
  on-surface-variant: '#3c4a42'
  inverse-surface: '#263143'
  inverse-on-surface: '#ecf1ff'
  outline: '#6c7a71'
  outline-variant: '#bbcabf'
  surface-tint: '#006c49'
  primary: '#006c49'
  on-primary: '#ffffff'
  primary-container: '#10b981'
  on-primary-container: '#00422b'
  inverse-primary: '#4edea3'
  secondary: '#4b41e1'
  on-secondary: '#ffffff'
  secondary-container: '#645efb'
  on-secondary-container: '#fffbff'
  tertiary: '#855300'
  on-tertiary: '#ffffff'
  tertiary-container: '#e29100'
  on-tertiary-container: '#523200'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#6ffbbe'
  primary-fixed-dim: '#4edea3'
  on-primary-fixed: '#002113'
  on-primary-fixed-variant: '#005236'
  secondary-fixed: '#e2dfff'
  secondary-fixed-dim: '#c3c0ff'
  on-secondary-fixed: '#0f0069'
  on-secondary-fixed-variant: '#3323cc'
  tertiary-fixed: '#ffddb8'
  tertiary-fixed-dim: '#ffb95f'
  on-tertiary-fixed: '#2a1700'
  on-tertiary-fixed-variant: '#653e00'
  background: '#f9f9ff'
  on-background: '#111c2d'
  surface-variant: '#d8e3fb'
typography:
  headline-xl:
    fontFamily: Lexend
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-xl-mobile:
    fontFamily: Lexend
    fontSize: 26px
    fontWeight: '700'
    lineHeight: 34px
    letterSpacing: -0.01em
  headline-lg:
    fontFamily: Lexend
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Lexend
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  headline-sm:
    fontFamily: Lexend
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
  body-lg:
    fontFamily: Manrope
    fontSize: 16px
    fontWeight: '500'
    lineHeight: 26px
  body-md:
    fontFamily: Manrope
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 22px
  body-sm:
    fontFamily: Manrope
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 18px
  label-lg:
    fontFamily: Manrope
    fontSize: 14px
    fontWeight: '700'
    lineHeight: 20px
    letterSpacing: 0.02em
  label-md:
    fontFamily: Manrope
    fontSize: 12px
    fontWeight: '700'
    lineHeight: 16px
    letterSpacing: 0.04em
  label-sm:
    fontFamily: Manrope
    fontSize: 10px
    fontWeight: '700'
    lineHeight: 14px
    letterSpacing: 0.05em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  margin: 1rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style

The design system is crafted for primary and secondary students preparing for Uganda National Examinations Board (UNEB) assessments (PLE, UCE, and UACE). The brand personality radiates academic optimism, structured motivation, approachable discipline, and joyful gamification. Learning transitions from stressful cramming into an engaging daily habit through tactile progress tracking, milestone celebrations, and vibrant, subject-coded visual pathways.

The aesthetic blends **Modern Elevated Clarity** with **Playful Gamified EdTech**. Rooted in high-retention readability, the interface balances generous whitespace and pristine white surface cards with energetic subject accents: disciplined emerald for success and biology, deep royal indigo for core humanities and focus sessions, warm amber for revision streaks and mathematics, coral salmon for physics and urgent deadlines, and sky cyan for digital mock tests. 

The emotional response is one of empowered confidence, clarity under exam pressure, and daily encouragement.

## Colors

The palette leverages a crisp, high-luminance light mode optimized for outdoor and classroom readability on mobile devices.

### Role Definitions
- **Primary (`#10B981` Emerald)**: Represents mastery, verified answers, active revision status, and UNEB syllabus progress. Used for primary CTA buttons, success indicators, and completion milestones.
- **Secondary (`#4F46E5` Royal Indigo)**: Represents academic rigor, deep work sessions, main subject headers, and primary navigation active anchors.
- **Tertiary (`#F59E0B` Sunny Amber)**: Drives the gamification engine—streak flames, achievement badges, leaderboards, and revision warnings.
- **Accent Coral (`#F43F5E`)**: Used for timed test alerts, flashcard flips, quiz error corrections, and chemistry/physics modules.
- **Accent Sky Cyan (`#0EA5E9`)**: Assigned to past-paper downloads, study group collaboration, and audio lesson tracks.
- **Neutral (`#1E293B` Slate)**: Delivers WCAG AAA-compliant text readability on bright white card structures, preventing optical eye strain during extended night revision.
- **Surface Canvas (`#F8FAFC`)**: A calm, ultra-light slate backing that makes floating content containers pop with separation.

## Typography

The type system pairs **Lexend** for headlines and titles with **Manrope** for body reading and interactive meta-labels. 

Lexend was empirically designed to reduce visual crowding and enhance reading fluency, making it an ideal choice for high-stakes UNEB educational assessments where comprehension speed is vital. Manrope provides a contemporary geometric rhythm with open apertures, ensuring mathematical formulas, historical passages, and multiple-choice questions remain hyper-legible on compact screens. Numbers are rendered with tabular clarity in quiz timers and scorecard displays.

## Layout & Spacing

The layout is built around a mobile-first fluid single-column structure that adapts to a 4-column system on compact screens and scales to an 8-to-12 column centered grid on study tablets and laptops.

- **Canvas Margins**: Set to `1rem` (16px) on mobile viewports to maximize question card surface area, expanding to `1.5rem` (24px) on tablets.
- **Gutters**: Fixed at `1rem` (16px) between grid items (e.g., dual-column subject grids such as Biology, Chemistry, Mathematics, English).
- **Component Padding Scale**:
  - `space-xs` (4px): Inner badge padding, micro-icon gaps.
  - `space-sm` (8px): Button icon-to-label separation, chip tag padding.
  - `space-md` (16px): Card internal padding, list item vertical gaps.
  - `space-lg` (24px): Inter-section spacing, quiz container margins.
  - `space-xl` (32px): Lesson header margins and modal sheet offsets.

## Elevation & Depth

Visual hierarchy is maintained via clean white container cards elevated against the tinted `#F8FAFC` foundation using soft ambient shadows. 

- **Level 0 (Flat Canvas)**: Neutral surface canvas (`#F8FAFC`) with no shadow.
- **Level 1 (Subject Cards, Question Containers, List Tiles)**: Pure white (`#FFFFFF`) with a dual-stage ambient drop shadow: `0 4px 12px -2px rgba(30, 41, 59, 0.05), 0 2px 6px -1px rgba(30, 41, 59, 0.03)`. Border outlines are subtle (`1px solid #E2E8F0`).
- **Level 2 (Active Quizzes, Header Banners, Floating Bottom Nav)**: `0 10px 25px -5px rgba(16, 185, 129, 0.12), 0 8px 10px -6px rgba(30, 41, 59, 0.04)`.
- **Level 3 (Modal Bottom Sheets, Gamification Confetti Overlays, Dialogs)**: `0 20px 35px -10px rgba(15, 23, 42, 0.18)`.

## Shapes

The design system enforces a roundedness level of `2` (`0.5rem` / 8px baseline). 

- **Cards & Task Containers (`rounded-lg`)**: 16px (`1rem`) border radius, creating approachable, non-intimidating exam revision cards.
- **Buttons & Input Fields (`rounded`)**: 8px (`0.5rem`) for crisp, responsive form inputs and answer toggles.
- **Pill Badges & Tags (`rounded-full`)**: 9999px fully rounded geometry for streak counters, grade status badges ("Distinction 1", "Credit 3"), and subject category pills.
- **Progress Fill Bars**: Soft rounded ends conforming to inner container radii.

## Components

### Buttons
- **Primary CTA**: Emerald green `#10B981` solid background with pure white `#FFFFFF` Lexend text, `0.5rem` radius, 48px height for thumb-friendly mobile interaction. Subtle click state scale down (`0.98`).
- **Secondary CTA**: Royal Indigo `#4F46E5` fill for secondary actions like "View UNEB Answer Guide" or "Download Past Paper PDF".
- **Ghost / Outlined Buttons**: Transparent fill, 1.5px border matching the brand color, used for secondary drill questions.

### Question & Subject Cards
- Crisp white surface cards with soft 16px corner radii.
- Subject cards display a 4px solid top or side accent bar in the designated subject color (Indigo for Mathematics, Emerald for Biology, Coral for Physics, Cyan for Geography).
- Tap feedback includes elevation increase from Level 1 to Level 2.

### Chips & Gamification Badges
- **Pill Badges**: Pastel background (15% tint of accent color) paired with a bold solid text color (e.g., `#FEF3C7` background with `#D97706` text for streak counts).
- **Topic Chips**: Small rounded-full interactive filters with a `1px` border in `#E2E8F0`, shifting to solid `#10B981` text and border upon selection.

### Form Inputs & Multiple Choice Selectors
- **Text Inputs**: 48px height, 8px radius, `#FFFFFF` background with `#E2E8F0` border. Active focus ring displays a 2px `#10B981` glow with zero offset.
- **Multiple Choice Radios/Tiles**: Full-width selectable card tiles with a 12px radius. In unselected state: border `1.5px solid #E2E8F0`. When selected: border `2px solid #10B981`, background `#ECFDF5`, and an animated green check badge.

### Progress & Streak Trackers
- Segmented linear progress bars featuring animated rounded caps that visually communicate completion toward UNEB syllabus goals (e.g., "70% of O-Level Chemistry Completed").