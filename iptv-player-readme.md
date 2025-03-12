# IPTV Player

A modern Android IPTV player application built with Jetpack Compose and designed following Clean Architecture principles. This application provides a seamless streaming experience for M3U playlists while serving as an excellent reference for modern Android development practices.

![IPTV Player Banner](https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQFcx2m6CvCidw3B6lyuVj4AXOQCI3iclIEKQ&sr)

## Features

- **M3U Playlist Support**: Load and parse M3U/M3U8 playlists from local storage
- **Live TV Streaming**: Smooth playback of IPTV channels
- **Channel Navigation**: Easily switch between channels with next/previous controls
- **Search Functionality**: Quickly find channels by name, group, or ID
- **Media Notifications**: Control playback from the notification area
- **Fullscreen Mode**: Toggle between normal and fullscreen viewing
- **Channel Information**: Display metadata from the M3U playlist including channel logos
- **Media Caching**: Improve loading times with smart media caching

## Architecture Overview

This project strictly follows Clean Architecture principles, organized into three distinct layers:

### Domain Layer

The core of the application containing business logic, independent of any external frameworks:

- **Models**: Data classes like `M3uEntry`
- **Repositories Interfaces**: Defines data operations without implementation details
- **Use Cases**: Single-responsibility classes for business operations
  - `LoadPlaylistUseCase`
  - `PlayMediaUseCase`
  - `DownloadImageUseCase`
  - etc.

### Data Layer

Implements the interfaces defined in the domain layer:

- **Repositories**: Concrete implementations of domain repositories
  - `M3uRepositoryImpl`
  - `MediaControllerImpl`
  - `ImageRepositoryImpl`
- **Data Sources**: Local data handling
  - `M3uLocalDataSource`
  - `M3uParser`
- **Cache Implementation**: Media content caching with `CacheImpl`

### Presentation Layer

User interface components built with Jetpack Compose following MVVM:

- **ViewModels**: Manages UI state and business logic
  - `MainViewModel`
- **Screens**: Jetpack Compose UI components
  - `MainScreen`
  - `VideoPlayerSurface`
  - `ChannelItem`

## Technical Stack

- **UI Framework**: Jetpack Compose
- **Architecture Pattern**: MVVM with Clean Architecture
- **Media Playback**: ExoPlayer (Media3)
- **Dependency Injection**: Hilt
- **Concurrency**: Kotlin Coroutines & Flow
- **Image Loading**: Coil
- **Navigation**: Jetpack Navigation Compose

## Learning Value

This project serves as an excellent learning resource for:

1. **Clean Architecture in Android**: See a real-world implementation of Clean Architecture with clear separation of concerns
2. **MVVM with Jetpack Compose**: Learn how ViewModels interact with Compose UI
3. **State Management**: Observe how app state is managed using Kotlin Flow and State
4. **ExoPlayer Integration**: Understand how to incorporate ExoPlayer for media playback
5. **Dependency Injection**: See Hilt in action with proper module organization
6. **Kotlin Coroutines**: Study asynchronous programming with coroutines and Flow
7. **Repository Pattern**: Learn how to abstract data sources behind repositories

## Getting Started

### Prerequisites

- Android Studio Hedgehog or newer
- Android SDK 21+
- Kotlin 1.8.0+

### Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/ip-tv-player.git
```

2. Open the project in Android Studio

3. Build and run the application on your device or emulator

## Usage

1. Launch the application
2. Tap on "Open Playlist" to select an M3U file from your device
3. Browse through the channel list or use the search bar to find specific channels
4. Tap on a channel to begin playback
5. Use the on-screen controls to manage playback and toggle fullscreen

## Contributing

Contributions are welcome! If you'd like to contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- [ExoPlayer](https://exoplayer.dev/) for the powerful media playback capabilities
- [Jetpack Compose](https://developer.android.com/jetpack/compose) for the modern UI toolkit
- [Hilt](https://dagger.dev/hilt/) for dependency injection
- All contributors who have helped improve this project
